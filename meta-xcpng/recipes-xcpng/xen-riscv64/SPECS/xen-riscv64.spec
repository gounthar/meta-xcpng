# riscv64 build of Xen and its tools from the RISC-V Xen tree (Baptiste
# Le Duc's xapi/investigation branch plus the gounthar/xcpng-riscv64
# patches), for meta-xcpng on AlmaLinux Kitten. Experimental.
#
# The x86 spec (xen.spec) builds stock Xen 4.21.2 with XCP-ng's patch
# queue and the x86 HVM pieces (rombios, qemu-system-i386, OVMF, iPXE).
# Stock 4.21.2 has no RISC-V code in libxenlight or libxenguest, and the
# patch queue does not apply to this tree, so this spec builds the tree as
# it is. Package names follow xen.spec, so that xapi's BuildRequires
# (xen-devel, xen-dom0-libs-devel, xen-ocaml-devel) resolve.

%global rev 9ede04b70e
# First build: no debuginfo, find-debuginfo would also process the
# hypervisor image in /boot
%global debug_package %{nil}

Name:    xen
Version: 4.18.0
Release: 0.riscv64.20261003.git%{rev}%{?dist}
Summary: Xen hypervisor and tools (RISC-V port, experimental)
License: GPL-2.0-only AND LGPL-2.1-only AND MIT AND BSD-2-Clause
URL:     https://gitlab.com/xen-project/people/baptleduc/xen
Source0: xen-riscv64-%{rev}.tar.gz
Source1: xen-riscv64.config
ExclusiveArch: riscv64

BuildRequires: gcc
BuildRequires: make
BuildRequires: bison
BuildRequires: flex
BuildRequires: perl-interpreter
BuildRequires: python3-devel
BuildRequires: python3-setuptools
BuildRequires: libuuid-devel
BuildRequires: zlib-devel
BuildRequires: ncurses-devel
BuildRequires: yajl-devel
BuildRequires: json-c-devel
BuildRequires: libempserver-devel
BuildRequires: libfdt-devel
BuildRequires: iasl
BuildRequires: bzip2-devel
BuildRequires: xz-devel
BuildRequires: lzo-devel
BuildRequires: libzstd-devel
BuildRequires: lz4-devel
BuildRequires: ocaml
BuildRequires: ocaml-findlib
BuildRequires: ocaml-compiler-libs
BuildRequires: systemd-devel

%description
The Xen hypervisor and its dom0 tools, built for riscv64 from the RISC-V
Xen tree. Experimental: for the XCP-ng on RISC-V work, not for production.

%package hypervisor
Summary: Xen hypervisor image (riscv64)
%description hypervisor
The Xen hypervisor image for riscv64.

%package devel
Summary: Xen public headers
%description devel
Public headers of the Xen hypervisor interface.

%package dom0-libs
Summary: Xen dom0 libraries
Provides: xen-libs = %{version}-%{release}
%description dom0-libs
Shared libraries used by the dom0 toolstack (libxenctrl, libxenguest,
libxenstore, libxenlight and the others).

%package dom0-libs-devel
Summary: Development files for the Xen dom0 libraries
Requires: xen-dom0-libs = %{version}-%{release}
Requires: xen-devel = %{version}-%{release}
Provides: xen-libs-devel = %{version}-%{release}
%description dom0-libs-devel
Headers, unversioned links and pkg-config files for the Xen dom0 libraries.

%package ocaml-libs
Summary: Runtime parts of the Xen OCaml bindings
Requires: xen-dom0-libs = %{version}-%{release}
%description ocaml-libs
Shared stubs of the OCaml bindings to the Xen libraries.

%package ocaml-devel
Summary: OCaml bindings to the Xen libraries
Requires: xen-ocaml-libs = %{version}-%{release}
%description ocaml-devel
OCaml bindings to the Xen libraries (xenctrl, xenstore, eventchn, mmap...).

%package tools
Summary: Xen dom0 tools
Requires: xen-dom0-libs = %{version}-%{release}
Provides: xen-dom0-tools = %{version}-%{release}
# xapi-core requires oxenstored; this package ships /usr/sbin/oxenstored
Provides: oxenstored = %{version}-%{release}
%description tools
Dom0 tools: xl, xenstored, xenconsoled, xenguest and the rest.

%prep
%setup -q -n xen-riscv64-%{rev}

%build
export XEN_TARGET_ARCH=riscv64
export PYTHON=%{__python3}
# --with-xenstored=xenstored: the default the upstream units launch
# (launch-xenstore, XENSTORED in /etc/sysconfig/xencommons). In daemon
# mode under systemd the unit waits for READY=1, which this oxenstored
# never sends: xenstored.service fails with result 'protocol' and its
# cgroup, oxenstored included, is killed. The C xenstored notifies.
# oxenstored is still built and shipped.
%configure --enable-ocamltools \
           --enable-systemd \
           --with-xenstored=xenstored \
           --disable-seabios \
           --disable-stubdom \
           --disable-docs \
           --disable-qemu-traditional \
           --with-system-ipxe=%{_datadir}/ipxe/ipxe.bin \
           --with-system-ovmf=%{_datadir}/edk2/OVMF.fd \
           IASL=%{_bindir}/iasl
%{make_build} build-tools
# The hypervisor is built with its own flags, as in xen.spec
unset CFLAGS
unset LDFLAGS
mkdir xen/build-rv64
cp %{SOURCE1} xen/build-rv64/.config
# Native builds run xen/include/Makefile's public-header checks
# (headers*.chk), which cross builds skip. RISC-V's public headers do not
# pass them yet (callback.h: unknown type xen_callback_t, not defined in
# arch-riscv.h). A compile arch other than the target skips the checks,
# as in a cross build; its only other effect is the HOSTCC default.
# The real fix belongs in the RISC-V public headers.
%{make_build} -C xen O=build-rv64 XEN_COMPILE_ARCH=riscv64-pkg olddefconfig
%{make_build} -C xen O=build-rv64 XEN_COMPILE_ARCH=riscv64-pkg build

%install
export XEN_TARGET_ARCH=riscv64
%{make_build} DESTDIR=%{buildroot} install-tools
install -D -m 644 xen/build-rv64/xen %{buildroot}/boot/xen-%{version}-%{release}
# As xen.spec does (%exclude): do not ship Xen's own OCaml xenstore and
# xenbus libraries. They shadow the xs-opam xenstore that xapi builds
# against (dune: Library "xenstore.unix" not found). oxenstored is a
# native executable and has them linked in.
rm -rf %{buildroot}%{_libdir}/ocaml/xenstore %{buildroot}%{_libdir}/ocaml/xenbus
# Split the installed tree by pattern; everything not claimed goes to -tools
cd %{buildroot}
find . \( -type f -o -type l \) | sed 's|^\.||' | sort > %{_builddir}/all.files
grep -E '^/usr/include/xen/' %{_builddir}/all.files > %{_builddir}/devel.files || :
grep -E '^%{_libdir}/lib[^/]*\.so\.' %{_builddir}/all.files > %{_builddir}/dom0-libs.files || :
grep -E '^%{_libdir}/lib[^/]*\.(so|a)$|^%{_libdir}/pkgconfig/|^/usr/include/[^/]+\.h$|^/usr/include/xen(store|ctrl|guest)?[-_/]' %{_builddir}/all.files \
    | grep -vE '^/usr/include/xen/' > %{_builddir}/dom0-libs-devel.files || :
# ocamlfind installs each C stub (dll*_stubs.so) in its library's own
# directory, not in a shared stublibs/
grep -E '^%{_libdir}/ocaml/.*/dll[^/]*\.so$' %{_builddir}/all.files > %{_builddir}/ocaml-libs.files || :
grep -E '^%{_libdir}/ocaml/' %{_builddir}/all.files | grep -vE '/dll[^/]*\.so$' > %{_builddir}/ocaml-devel.files || :
cat %{_builddir}/devel.files %{_builddir}/dom0-libs.files %{_builddir}/dom0-libs-devel.files \
    %{_builddir}/ocaml-libs.files %{_builddir}/ocaml-devel.files \
    | sort | comm -23 %{_builddir}/all.files - \
    | grep -vE '^/boot/|^%{python3_sitearch}/' > %{_builddir}/tools.files
# Empty directories install-tools creates (/var/lib/xen for the libxl
# lock file, /var/log/xen...): not matched by the file lists above
find . -type d -empty | sed 's|^\.||' | grep -vE '^/boot' | sed 's|^|%%dir |' >> %{_builddir}/tools.files

%files hypervisor
/boot/xen-%{version}-%{release}

%files devel -f %{_builddir}/devel.files
%files dom0-libs -f %{_builddir}/dom0-libs.files
%files dom0-libs-devel -f %{_builddir}/dom0-libs-devel.files
%files ocaml-libs -f %{_builddir}/ocaml-libs.files
%files ocaml-devel -f %{_builddir}/ocaml-devel.files
%files tools -f %{_builddir}/tools.files
# Claimed by glob: byte-compiled .pyc files are added after %%install
%{python3_sitearch}/*

%changelog
* Sun Oct 04 2026 Bruno Verachten <gounthar@gmail.com> - 4.18.0-0.riscv64.20261003.git9ede04b70e
- Default to the C xenstored, which notifies systemd
- Package the empty directories install-tools creates (/var/lib/xen)

* Sat Oct 03 2026 Bruno Verachten <gounthar@gmail.com> - 4.18.0-0.riscv64.20261003.git9ede04b70e
- First riscv64 build from the RISC-V Xen tree (experimental)
