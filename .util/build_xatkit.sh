# This is required because Xatkit is not yet on Maven Central or similar

# Print a message
e() {
    echo -e "$1"
}

main() {

	e "Building Xatkit Parent"
    cd /tmp
    git clone https://github.com/xatkit-bot-platform/xatkit.git
    cd xatkit
    git submodule update --init --recursive
    mvn clean install -DskipTests > /dev/null
    e "Done"

}

main