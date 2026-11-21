# ocaml-srpm-macros is excluded from the meta-almalinux bridge along with
# ocaml (meta-xcpng ships its own), but redhat-rpm-config requires it, and the
# riscv64 build-env images install redhat-rpm-config from the layer.  It only
# carries RPM macros and requires nothing; same layout as a generated recipe.
# riscv64 only: other arches build their image from the live repos.

inherit dnf-bridge

COMPATIBLE_MACHINE = "riscv64"

PN = "ocaml-srpm-macros"
PV = "10"
PR = "4.el10"
PACKAGES = " \
 ocaml-srpm-macros \
 "

SRC_URI = "${ALMALINUXSRC_MIRROR}/AppStream/Source/Packages/ocaml-srpm-macros-10-4.el10.src.rpm;name=src;unpack=0"
SRC_URI[src.sha256sum] = "0c9449e4760a1bf8cae6a0c96eb15865ecd03179cdb456b996dafb4e2733f412"

SRC_URI:append = " ${ALMALINUX_MIRROR}/AppStream/riscv64/os/Packages/ocaml-srpm-macros-10-4.el10.noarch.rpm;name=riscv64_ocaml-srpm-macros;unpack=0"
SRC_URI[riscv64_ocaml-srpm-macros.sha256sum] = "f9ee48e40c5bc2dcdd87cb85a3d69088527801cf5c309f565753f834df097cb7"

RDEPENDS:ocaml-srpm-macros = " \
 "
