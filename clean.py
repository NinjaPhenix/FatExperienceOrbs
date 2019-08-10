from os import system

system("rmdir /q /s .gradle\loom-cache")
system("gradlew --stop")
system("gradlew genSources")
input("Press any key to exit.")
