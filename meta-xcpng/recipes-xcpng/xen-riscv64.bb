# Xen and its tools for riscv64, from the RISC-V Xen tree (see the spec).
# Package names follow xen.spec; local.conf prefers this recipe on riscv64.
inherit srpm-intree
RDEPENDS:xen-dom0-libs = "lzo yajl json-c"
RDEPENDS:xen-dom0-libs-devel = "xen-dom0-libs xen-devel"
RDEPENDS:xen-ocaml-libs = "ocaml-runtime xen-dom0-libs"
RDEPENDS:xen-ocaml-devel = "ocaml xen-ocaml-libs"
RDEPENDS:xen-tools = "xen-dom0-libs libempserver json-c"
