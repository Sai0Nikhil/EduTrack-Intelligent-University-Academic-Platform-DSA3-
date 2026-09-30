package edutrack;

import edutrack.ui.EduTrackCLI;
import edutrack.ui.EduTrackGUI;

/**
 * Universal Entry Point for the EduTrack Academic Platform.
 * Supports launching GUI, CLI, or Automated Headless Test Suite.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 0) {
            String flag = args[0].toLowerCase();
            if ("--cli".equals(flag) || "-c".equals(flag)) {
                EduTrackCLI.main(args);
                return;
            } else if ("--test".equals(flag) || "-t".equals(flag)) {
                EduTrackGUI.main(new String[]{"--test"});
                return;
            } else if ("--gui".equals(flag) || "-g".equals(flag)) {
                EduTrackGUI.main(new String[]{});
                return;
            }
        }

        // Default behavior: launch GUI if desktop display is available, else CLI
        try {
            if (!java.awt.GraphicsEnvironment.isHeadless()) {
                EduTrackGUI.main(args);
            } else {
                EduTrackCLI.main(args);
            }
        } catch (Exception e) {
            EduTrackCLI.main(args);
        }
    }
}
