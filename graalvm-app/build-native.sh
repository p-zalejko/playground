last_commit_hash="latest"
# last_commit_hash=$(git rev-parse --short HEAD)
docker build -f Dockerfiles/Dockerfile.native --build-arg APP_FILE=target/graalvm-app -t pzalejko/hello-world-native:$last_commit_hash .