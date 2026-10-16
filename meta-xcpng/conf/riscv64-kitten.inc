# riscv64 on an AlmaLinux Kitten base, through the dnf-bridge riscv64 bridge.
#
# Not pulled in automatically: add this line to build/conf/local.conf
# after setting MACHINE = "riscv64":
#
#   require conf/riscv64-kitten.inc
#
# Break Kitten-bridge runtime cycles
RDEPENDS:rpm-libs:remove = "rpm-plugin-audit"
RDEPENDS:rpm-libs:remove = "rpm-plugin-syslog"
RDEPENDS:almalinux-kitten-release:remove = "almalinux-kitten-repos"
RPROVIDES:systemd:remove = "virtual/group_dialout_"
RPROVIDES:systemd:remove = "virtual/group_tty_"
# xcp-ng-build-env, riscv64/kitten branch (riscv64 platform + Kitten Dockerfile)
SRC_URI:pn-build-env = "git://github.com/gounthar/xcp-ng-build-env.git;protocol=https;branch=riscv64/kitten"
SRCREV:pn-build-env = "47bab3f2299e0cdd894d7c6fcf840d83064282ca"
# riscv64: yajl from our in-tree yajl-src (Kitten has no riscv64 yajl binary)
PREFERRED_PROVIDER_rpm/yajl = "yajl-src"
PREFERRED_PROVIDER_rpm/yajl-devel = "yajl-src"
# Kitten bridge cycle cmake <-> cmake-data (hit building yajl-src)
RDEPENDS:cmake-data:remove = "cmake"
# riscv64: Xen from the RISC-V tree (xen-riscv64 recipe) for the names xapi needs
PREFERRED_PROVIDER_rpm/xen-devel = "xen-riscv64"
PREFERRED_PROVIDER_rpm/xen-dom0-libs = "xen-riscv64"
PREFERRED_PROVIDER_rpm/xen-dom0-libs-devel = "xen-riscv64"
PREFERRED_PROVIDER_rpm/xen-ocaml-libs = "xen-riscv64"
PREFERRED_PROVIDER_rpm/xen-ocaml-devel = "xen-riscv64"
PREFERRED_PROVIDER_rpm/xen-hypervisor = "xen-riscv64"
# riscv64: xxhash from our in-tree xxhash-src (EPEL has no riscv64)
PREFERRED_PROVIDER_rpm/xxhash = "xxhash-src"
PREFERRED_PROVIDER_rpm/xxhash-libs = "xxhash-src"
PREFERRED_PROVIDER_rpm/xxhash-devel = "xxhash-src"
PREFERRED_PROVIDER_rpm/xen-tools = "xen-riscv64"
# riscv64 experiment: hide the stock xen recipe (x86-only spec); xen-riscv64 provides the same package names.
# Without this, runtime names like xen-tools resolve to the higher-versioned stock xen, which pulls edk2/qemu/ipxe.
BBMASK += "/recipes-xcpng/xen\.bb$"
# riscv64: hide the stock xapi recipe too; xapi-riscv64 provides the same names (xapi-idl-devel...),
# and the stock one would win them on its higher release.
BBMASK += "/recipes-xcpng/xapi\.bb$"
# xs-opam-repo links openssl, curl, libev, libffi, gmp, libpci; its recipe only declares opam and libev-devel
RDEPENDS:xs-opam-repo:append = " openssl-libs libcurl libev libffi gmp pciutils-libs"
# riscv64: busybox from our in-tree busybox-src (Kitten has no busybox)
PREFERRED_PROVIDER_rpm/busybox = "busybox-src"
# Kitten bridge cycle glibc-static <-> libxcrypt-static (hit building busybox-src)
RDEPENDS:libxcrypt-static:remove = "glibc-static"
# Kitten bridge cycle pyproject-rpm-macros <-> pyproject-srpm-macros (hit building xcp-python-libs)
RDEPENDS:pyproject-srpm-macros:remove = "pyproject-rpm-macros"
# riscv64: hide the stock qemu recipe (x86 HVM device model); qemu-riscv64 builds the PV-backend-only
# QEMU for the xenfb console (xcpng-riscv64 #92) under the same package name.
BBMASK += "/recipes-xcpng/qemu\.bb$"
