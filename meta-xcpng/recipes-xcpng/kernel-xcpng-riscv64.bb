# Linux for the riscv64 dom0, from the RISC-V Linux tree (see the spec).
# Separate from kernel-xcpng (x86, 4.19): nothing requires a kernel, the
# dom0 image installs this one by name.
inherit srpm-intree
SPECFILE = "SPECS/kernel-xcpng-riscv64.spec"
