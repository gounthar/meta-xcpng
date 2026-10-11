# riscv64 dom0 kernel for Xen, from the RISC-V Linux tree (Baptiste Le
# Duc's linux-xen-riscv 6.18-xen-guest-support branch, 048308d86) plus the
# gounthar/xcpng-riscv64 fixes under review there (PRs #2-#5: gnttab init,
# unmap_domain_gfn_range, kbdfront in xen_defconfig, upcall ack). The tree
# is branch dom0/all-fixes-20261007 of gounthar/linux-xen-riscv.
#
# kernel-xcpng (4.19, x86) does not apply here, and Kitten's own 6.12
# kernel has no Xen support at all (no CONFIG_XEN in its config). The
# config is the one the lab dom0 kernel was built with (Xen backends,
# bridge and the root disk drivers built in, so no initramfs), with two
# changes: no debug info (it only feeds debuginfo, which this spec does
# not package, and makes every object larger to build under emulation)
# and a fixed LOCALVERSION.
#
# Xen loads this kernel as the dom0 module: install it where the boot
# setup can find it. No kernel-install and no initramfs. Experimental.

%global rev e42e2d799
%global krel 0.riscv64.20261011.git%{rev}
%global uname %{version}-%{krel}.riscv64
%global debug_package %{nil}

Name:    kernel-xcpng-riscv64
Version: 6.18.0
Release: %{krel}.1%{?dist}
Summary: Linux kernel for a Xen dom0 on riscv64 (experimental)
License: GPL-2.0-only
URL:     https://github.com/gounthar/linux-xen-riscv/tree/dom0/all-fixes-20261007
Source0: linux-xen-riscv64-%{rev}.tar.gz
Source1: kernel-xcpng-riscv64.config
ExclusiveArch: riscv64

BuildRequires: gcc
BuildRequires: make
BuildRequires: bc
BuildRequires: bison
BuildRequires: flex
BuildRequires: perl-interpreter
BuildRequires: python3
BuildRequires: openssl-devel
BuildRequires: elfutils-libelf-devel
BuildRequires: kmod
BuildRequires: gzip
BuildRequires: diffutils
BuildRequires: findutils

Requires(post): kmod
Provides: kernel-uname-r = %{uname}

%description
Linux 6.18 with the RISC-V Xen guest and dom0 support of the
linux-xen-riscv tree, configured for the dom0 of XCP-ng on RISC-V. Xen
loads it as the dom0 kernel. Experimental: for the XCP-ng on RISC-V work,
not for production.

%prep
%setup -q -n linux-xen-riscv64-%{rev}

%build
cp %{SOURCE1} .config
scripts/config --file .config \
  --set-str LOCALVERSION "-%{krel}.riscv64" -d LOCALVERSION_AUTO \
  -d DEBUG_INFO_DWARF5 -d DEBUG_INFO_DWARF4 -d DEBUG_INFO_DWARF_TOOLCHAIN_DEFAULT \
  -e DEBUG_INFO_NONE
%{__make} olddefconfig
test "$(%{__make} -s kernelrelease)" = "%{uname}"
%{make_build} Image.gz modules

%install
install -d -m 755 %{buildroot}/boot
install -m 644 arch/riscv/boot/Image.gz %{buildroot}/boot/vmlinuz-%{uname}
install -m 644 System.map %{buildroot}/boot/System.map-%{uname}
install -m 644 .config %{buildroot}/boot/config-%{uname}
# Stable name for the U-Boot boot script on the dom0 disk, as the x86
# kernel.spec ships /boot/vmlinuz-<short>-xen
ln -s vmlinuz-%{uname} %{buildroot}/boot/vmlinuz-xcpng
%{__make} INSTALL_MOD_PATH=%{buildroot} INSTALL_MOD_STRIP=1 DEPMOD=/bin/true modules_install
rm -f %{buildroot}/lib/modules/%{uname}/build %{buildroot}/lib/modules/%{uname}/source

%post
/sbin/depmod -a %{uname}

%files
/boot/vmlinuz-%{uname}
/boot/System.map-%{uname}
/boot/config-%{uname}
/boot/vmlinuz-xcpng
/lib/modules/%{uname}

%changelog
* Sun Oct 11 2026 Bruno Verachten <gounthar@gmail.com> - 6.18.0-0.riscv64.20261011.gite42e2d799.1
- Ship /boot/vmlinuz-xcpng, a stable name for the U-Boot boot script

* Sun Oct 11 2026 Bruno Verachten <gounthar@gmail.com> - 6.18.0-0.riscv64.20261011.gite42e2d799
- First riscv64 dom0 kernel build (experimental)
