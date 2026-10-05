# QEMU for riscv64 dom0, only as a Xen PV backend (xenfb console), from
# Julian Vetter's Arm branch of XCP-ng's QEMU (see the spec).
inherit srpm-intree
SRPM_NAME = "qemu"
SPECFILE = "SPECS/qemu-riscv64.spec"
PACKAGES = ""
RDEPENDS:qemu = "xen-dom0-libs"
