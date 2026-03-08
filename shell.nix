{
  pkgs ? import (fetchTarball "https://github.com/NixOS/nixpkgs/archive/nixos-unstable.tar.gz") { },
}:

pkgs.mkShell {
  name = "fabric-mod-dev";

  buildInputs = with pkgs; [
    # Java Development Kit - Minecraft 1.20+ requires Java 21
    # For older versions (1.17-1.19), use jdk17
    jdk21

    # Gradle build system
    gradle

    # Useful development tools
    git
    uv
    blockbench
    viu

    # For running Minecraft client in dev
    # These provide OpenGL and audio support
    libGL
    glfw
    openal
    flite
    libpulseaudio
    alsa-lib
  ];

  # Environment variables for Java
  JAVA_HOME = "${pkgs.jdk21}";

  # Native library paths for Minecraft/LWJGL
  LD_LIBRARY_PATH = pkgs.lib.makeLibraryPath [
    pkgs.libGL
    pkgs.glfw
    pkgs.openal
    pkgs.libpulseaudio
    pkgs.alsa-lib
  ];

  shellHook = ''
    echo "🧱 Fabric Mod Development Environment"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "Java:   $(java -version 2>&1 | head -1)"
    echo "Gradle: $(gradle --version 2>&1 | grep '^Gradle' || echo 'available')"
    echo ""
    echo "Commands:"
    echo "  gradle build          - Build the mod"
    echo "  gradle runClient      - Run Minecraft with your mod"
    echo "  gradle runServer      - Run a server with your mod"
    echo "  gradle genSources     - Generate Minecraft sources for IDE"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
  '';
}
