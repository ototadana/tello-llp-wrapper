# Tello Low-Level Protocol Wrapper

A wrapper for [Tello Low-Level Protocol](https://tellopilots.com/wiki/protocol/) for Java applications.

## Usage

You can access the Tello Low-Level Protocol by simple
interface
of [Facade](./src/main/java/com/xpfriend/tydrone/core/Facade.java).

- **run** : Start communication with the Tello.
- **entryCommand** : Sends a command to Tello.
    - Supported Commands:
        - **takeoff**
        - **land**
        - **stick rx ry lx ly speed**
            - **rx:** (left)     -1  < 0 < 1 (right)
            - **ry:** (backward) -1  < 0 < 1 (forward)
            - **lx:** (down)     -1  < 0 < 1 (up)
            - **ly:** (ccw)      -1  < 0 < 1 (cw)
            - **speed:** 0 (slow) or 1 (fast)
        - **picture** - Take a picture and save the jpeg file in the `~/Downloads` folder.
- **pickImage** : Get a video frame image (960*720 RGB24) as a byte array.
- **getStates** : Get the Tello states as text like: `bat:%d;lit:%d;wifi:%d;yaw:%d`
- **setRecording** : If `true`, video recording will start; if `false`, the video will be saved in
  the `~/Downloads` folder.

## Example

For example, the operation "take off, take a picture, and land" would be the following code:

```java
public class Example {
    @Test
    public void testPicture() throws Exception {
        try (SimpleMain main = new SimpleMain()) {

            // start communication with Tello
            main.run();
            Thread.sleep(2000);

            // takeoff
            main.entryCommand("takeoff");
            Thread.sleep(3000);

            // take a picture
            main.entryCommand("picture");
            Thread.sleep(3000);

            // land
            main.entryCommand("land");
        }
    }
}
```

For more details, see: [Example.java](./src/test/java/com/xpfriend/tydrone/Example.java)

## Required Software

See [build.gradle](./build.gradle) for the software on which this software depends.

## License

This software is released under the MIT License, see [LICENSE](./LICENSE).

## Acknowledgements

This code is based on the achievements of the great pioneers:

- [Tello Development Wiki](https://tellopilots.com/wiki/index/)
- [TelloPy](https://github.com/hanyazou/TelloPy)
- [TelloLib](https://github.com/Kragrathea/TelloLib)
- [Gobot](https://gobot.io/)
- [https://qiita.com/mozzio369/items/8e0fb12dc30c493f5cc4](https://qiita.com/mozzio369/items/8e0fb12dc30c493f5cc4)
