#!/usr/bin/env bash
# This is required because Xatkit is not yet on Maven Central or similar

# Print a message
e() {
    echo -e "$1"
}

main() {

	# Do not print the build log, it is already available in the Xatkit build
    e "Building Messenger Platform"
    cd /tmp
    git clone https://github.com/xatkit-bot-platform/labs-ut-platform.git > /dev/null
    cd labs-ut-platform
    mvn install -DskipTests > /dev/null
    e "Done"


}

main