inherit srpm-intree

RDEPENDS:xen-dom0-libs = "lzo"
RDEPENDS:xen-dom0-libs-devel = "xen-dom0-libs"
RDEPENDS:xen-dom0-tests = "xen-dom0-libs"
RDEPENDS:xen-dom0-tools = "edk2 ipxe libempserver qemu"
RDEPENDS:xen-ocaml-libs = "ocaml-runtime xen-dom0-libs"
RDEPENDS:xen-ocaml-devel = "ocaml xen-ocaml-libs"
RDEPENDS:xen-oxenstored = "xen-dom0-libs"
