#! /bin/bash
# (c) https://github.com/MontiCore/monticore

# Stop on first error
set -e

# Iterate over all applications, generate and build them
dirs=$(find applications -mindepth 1 -maxdepth 1 -type d);
for d in $dirs; do
    # ignore hidden directories like .settings
    if [[ -d "${d}" && "${d}" =~ ^applications/\. ]] ; then
        echo "${d} is a hidden folder, ignoring..."
        continue
    fi

    echo "Building ${d}..."
    if [[ ! -d "${d}/target/generated-sources/" ]]; then
        echo "Generated sources not found, skipping..."
        continue
    fi

    cd $d/target/generated-sources/

    registry=$CI_REGISTRY_IMAGE
    imagename=$(basename $d)
    # to lower case
    imagename=${imagename,,}
    imageref="${registry}/${imagename}"
    
    echo "Registry: ${registry}"
    echo "Image name: ${imagename}"
    echo "Full image ref: ${imageref}"

    docker buildx build --pull --platform linux/amd64,linux/arm64,linux/arm/v7,linux/arm/v6 -t $imageref:latest --push .

    cd -
done
