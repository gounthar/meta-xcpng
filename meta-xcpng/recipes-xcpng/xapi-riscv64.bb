# xapi for riscv64, from the RISC-V xen-api fork (see the spec).
inherit srpm-intree
SRPM_NAME = "xapi"
SPECFILE = "SPECS/xapi-riscv64.spec"
PACKAGES = ""
# Runtime requirements BitBake cannot derive (srpm-intree): the OCaml module
# provides (ocaml(Stdlib__*) = hash) come from ocaml-runtime.
RDEPENDS:xapi-idl-devel = "ocaml-runtime xs-opam-repo glibc"
RDEPENDS:xapi-client-devel = "xapi-idl-devel xs-opam-repo ocaml-runtime"
