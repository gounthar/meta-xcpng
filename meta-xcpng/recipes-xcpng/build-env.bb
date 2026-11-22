# Recipe to create a build-env (and ultimately build packages from it).
# This is an XCP-ng *bootstrap* podman container to make sure we do not
# rely on any already-built XCP-ng package, just AlmaLinux ones.
#
# TODO: should actually be split between some generic support one one
# side, which the rpm class can rely on, and specific implementation
# for xcp-ng-build-env on another side.

SRC_URI = "git://github.com/xcp-ng/xcp-ng-build-env;protocol=https;nobranch=1"
SRCREV = "899b5a1c67c4a3936533023f031b49adc563b6c7"
S = "${UNPACKDIR}/git"

# Build the images from the dnf-bridge layer's own RPMs instead of the live
# repos, when BUILD_ENV_PACKAGES names a list of the packages they hold (one
# per line, found through FILESPATH; riscv64-kitten.inc sets it, since Kitten
# moves under a build).  do_layer_repo puts those packages and their runtime
# closure in a local repo, as do_builddeps_repo does for package builds, with
# the list as packages.list; build.sh --layer-repo installs from it alone.
# Unset: the live repos, as before.
BUILD_ENV_PACKAGES ?= ""
BUILD_ENV_LAYER_REPO = "${WORKDIR}/layer-repo"
# This recipe does not inherit rpm-base, which sets these for package
# recipes; dnfbridge.py reads them to walk the runtime-deps tree.
DEPLOY_DIR_RPMS ?= "${DEPLOY_DIR}/rpms"
RUNTIMEDEPSDIR ?= "${TMPDIR}/rtdeps"

def build_env_packages_file(d):
    name = d.getVar('BUILD_ENV_PACKAGES')
    if not name:
        return ''
    path = bb.utils.which(d.getVar('FILESPATH'), name)
    if not path:
        bb.fatal(f'BUILD_ENV_PACKAGES: {name} not found in FILESPATH')
    return path

def build_env_packages(d):
    path = build_env_packages_file(d)
    if not path:
        return []
    with open(path) as f:
        return [n for n in (line.split('#', 1)[0].strip() for line in f) if n]

python () {
    for pkg in build_env_packages(d):
        d.appendVarFlag('do_layer_repo', 'depends', f' rpm/{pkg}:do_deploy_runtimedeps_{pkg}')
}

addtask layer_repo after do_unpack before do_build_bootstrap do_build
do_layer_repo[file-checksums] = "${@'%s:True' % build_env_packages_file(d) if d.getVar('BUILD_ENV_PACKAGES') else ''}"
python do_layer_repo() {
    import dnfbridge
    repo = d.getVar('BUILD_ENV_LAYER_REPO')
    oe.path.remove(repo, recurse=True)
    pkgs = build_env_packages(d)
    if not pkgs:
        return
    recdepdict = {}  # binrpm -> recipe
    dnfbridge.accumulate_rdeps_from_list(d, recdepdict, pkgs)
    dnfbridge.create_dnfrepo_with_contents(d, repo, recdepdict.keys())
    with open(os.path.join(repo, 'packages.list'), 'w') as f:
        f.write('\n'.join(pkgs) + '\n')
    bb.process.run(['createrepo_c', '--compatibility', repo])
    bb.note(f'layer repo: {len(pkgs)} listed packages, {len(recdepdict)} with their closure')
}
BUILD_ENV_LAYER_REPO_ARG = "${@'--layer-repo ' + d.getVar('BUILD_ENV_LAYER_REPO') if d.getVar('BUILD_ENV_PACKAGES') else ''}"

addtask build_bootstrap after do_unpack
do_build_bootstrap() {
    env XCPNG_OCI_RUNNER=podman ${S}/container/build.sh \
        --platform "${CONTAINER_ARCH}" \
        --bootstrap \
        ${BUILD_ENV_LAYER_REPO_ARG} \
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
        ${BUILD_ENV_LAYER_REPO_ARG} \
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
