package me.bartholdy.endlessjump.GameAPI;

import net.minestom.server.coordinate.Pos;

public class CoordinateUtil {

    private static String[] prepareSerializedPosString(String posString) {
        if (!posString.startsWith("Pos["))
            throw new RuntimeException("Please provide a valid serialized pos object");

        return posString.replace("Pos[", "")
                .replace("]", "")
                .replace(",", "")
                .split(" ");
    }

    public static Pos posFromString(String posString) {
        String[] arr = prepareSerializedPosString(posString);
        if (arr.length == 3)
            return new Pos(
                    Double.parseDouble(arr[0].replace("x=", "")),
                    Double.parseDouble(arr[1].replace("y=", "")),
                    Double.parseDouble(arr[2].replace("z=", ""))
            );
        else if (arr.length == 5)
            return new Pos(
                    Double.parseDouble(arr[0].replace("x=", "")),
                    Double.parseDouble(arr[1].replace("y=", "")),
                    Double.parseDouble(arr[2].replace("z=", "")),
                    Float.parseFloat(arr[3].replace("yaw=", "")),
                    Float.parseFloat(arr[4].replace("pitch=", ""))
            );
        else throw new RuntimeException("Deserialized pos array object has a invalid length of " + arr.length);
    }

    public static boolean comparePos(Pos pos1, Pos pos2) {
        return pos1.blockX() == pos2.blockX() && pos1.blockZ() == pos2.blockZ();
    }
}
