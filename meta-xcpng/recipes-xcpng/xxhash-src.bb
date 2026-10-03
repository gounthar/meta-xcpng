# xxhash built from EPEL 10's source RPM (xxhash-0.8.4-1.el10_3.src.rpm, unchanged):
# EPEL has no riscv64 tree, and xapi links libxxhash.
inherit srpm-intree
SRPM_NAME = "xxhash"
PACKAGES = "${SRPM_NAME}"
