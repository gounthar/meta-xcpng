# package liblsan-static is in "devel" which should not be bridged as-is
PACKAGES:append = " liblsan-static"
SRC_URI:append = "\
 ${ALMALINUX_MIRROR}/devel/x86_64_v2/os/Packages/liblsan-static-14.4.1-3.el10.alma.2.x86_64_v2.rpm;name=x86_64_v2_liblsan-static;unpack=0 \
 ${ALMALINUX_MIRROR}/devel/aarch64/os/Packages/liblsan-static-14.4.1-3.el10.alma.2.aarch64.rpm;name=aarch64_liblsan-static;unpack=0 \
 ${ALMALINUX_MIRROR}/devel/riscv64/os/Packages/liblsan-static-14.4.1-3.el10.alma.2.riscv64.rpm;name=riscv64_liblsan-static;unpack=0 \
"
SRC_URI[x86_64_v2_liblsan-static.sha256sum] = "8b355a6d72e000864e6795a69a39dd7dd7e78e33663ba43868933a48e2406eb8"
SRC_URI[aarch64_liblsan-static.sha256sum] = "efb9062315f1fd5bbef2cc10c0ac3fcad07de620257c5958246c07f37fabdc70"
SRC_URI[riscv64_liblsan-static.sha256sum] = "d2c388bee2151e06d28bd1cb27a5f7c2baf146c9d96452b4a925d19ff49a738f"
