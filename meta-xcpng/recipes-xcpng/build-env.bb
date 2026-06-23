# Recipe to create a build-env (and ultimately build packages from it).
# This is an XCP-ng *bootstrap* podman container to make sure we do not
# rely on any already-built XCP-ng package, just AlmaLinux ones.
#
# TODO: should actually be split between some generic support one one
# side, which the rpm class can rely on, and specific implementation
# for xcp-ng-build-env on another side.

SRC_URI = "git://github.com/xcp-ng/xcp-ng-build-env;protocol=https;nobranch=1"
SRCREV = "dc49f45bd5f0c9c7f95832a36724b771019c92a9"
S = "${UNPACKDIR}/git"

addtask build_bootstrap after do_unpack
do_build_bootstrap() {
    env XCPNG_OCI_RUNNER=podman ${S}/container/build.sh \
        --platform "${CONTAINER_ARCH}" \
        --bootstrap \
        9.0
}
# FIXME: temporary until we separately fetch the required RPMs
do_build_bootstrap[network] = "1"

RECIPE_DEPLOY_DIR = "${DEPLOY_DIR}/${PN}"

addtask deploy after do_unpack
do_deploy() {
    rm -rf "${RECIPE_DEPLOY_DIR}"
    python3 -m venv "${RECIPE_DEPLOY_DIR}"
    . "${RECIPE_DEPLOY_DIR}"/bin/activate
    python3 -m pip install ${S}
}
# we don't want to depend too strictly on the python dependency chain, right?
# FIXME: or do we?
do_deploy[network] = "1"


DEPENDS = "xcp-ng-release"

addtask build after do_build_bootstrap
do_build[deptask] = "do_deploy"
do_build() {
    # Prepare dnf repo.  This is much simpled than do_builddeps_repo so
    # we don't care sharing code with it.
    builddeps_dir="${DEPLOY_DIR}/bdeps"
    rm -rf "$builddeps_dir"
    mkdir -p "$builddeps_dir"
    cp -la ${DEPLOY_DIR}/rpms/xcp-ng-release/RPMS/${PACKAGE_ARCH}/*.rpm "$builddeps_dir/"
    createrepo_c --compatibility "$builddeps_dir/"

    env XCPNG_OCI_RUNNER=podman ${S}/container/build.sh \
        --platform "${CONTAINER_ARCH}" \
        --isarpm \
        --add-repo "release:$builddeps_dir" \
        9.0
}
# FIXME: temporary until we separately fetch the required RPMs
do_build[network] = "1"

addtask deploy after do_unpack
RECIPE_DEPLOY_DIR = "${DEPLOY_DIR}/${PN}"
do_deploy() {
    rm -rf "${RECIPE_DEPLOY_DIR}"
    python3 -m venv "${RECIPE_DEPLOY_DIR}"
    . "${RECIPE_DEPLOY_DIR}"/bin/activate
    python3 -m pip install ${S}
}
# we don't want to depend too strictly on the python dependency chain, right?
# FIXME: or do we?
do_deploy[network] = "1"
