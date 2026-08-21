# package liblsan-static is in "devel" which should not be bridged as-is

PACKAGES:append = " liblsan-static"

SRC_URI:append = "\
 https://vault.almalinux.org/10.1/devel/x86_64_v2/os/Packages/liblsan-static-14.3.1-2.1.el10.alma.1.x86_64_v2.rpm;name=x86_64_v2_liblsan-static;unpack=0 \
 https://vault.almalinux.org/10.1/devel/aarch64/os/Packages/liblsan-static-14.3.1-2.1.el10.alma.1.aarch64.rpm;name=aarch64_liblsan-static;unpack=0 \
"
SRC_URI[x86_64_v2_liblsan-static.sha256sum] = "3b63bfcdf84e9ac565f9662d440b8f45fcb74823fb8f9ace0ea79eb6844cac44"
SRC_URI[aarch64_liblsan-static.sha256sum] = "446845ebb9d3ca8957ee58f36ca6a48b02ac9f36a00ce5d883188c3e24834070"
