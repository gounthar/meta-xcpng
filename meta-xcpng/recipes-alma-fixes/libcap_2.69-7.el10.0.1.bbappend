# Kitten has two libcap source releases: 2.69-7.el10.0.1 is built for
# x86_64_v2 only, 2.69-7.el10 for aarch64 and riscv64.  gen-dnf-proxy keeps
# the newest source and so bridges no riscv64 libcap at all, although systemd,
# dnf and most of the base system need libcap.so.2 (see unresolved.log).
# Add the riscv64 2.69-7.el10 RPMs to the bridged recipe; the release in their
# file names differs from PR, which only names the source package here.
PACKAGES:riscv64 = " \
 libcap \
 libcap-devel \
"
SRC_URI:append = "\
 ${ALMALINUX_MIRROR}/BaseOS/riscv64/os/Packages/libcap-2.69-7.el10.riscv64.rpm;name=riscv64_libcap;unpack=0 \
 ${ALMALINUX_MIRROR}/AppStream/riscv64/os/Packages/libcap-devel-2.69-7.el10.riscv64.rpm;name=riscv64_libcap-devel;unpack=0 \
"
SRC_URI[riscv64_libcap.sha256sum] = "0f90b4336813600174637e20a61703deffb4a77daf1a7ac8603ebd5860a408cb"
SRC_URI[riscv64_libcap-devel.sha256sum] = "159df98c07a4b24b96f94bcdc50ab46d228c181d43744e2ca01b0ef8f031e3ca"
