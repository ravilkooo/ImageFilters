import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class Main {

    BufferedReader br;
    StringTokenizer in;

    public String nextToken() throws IOException {
        while (in == null || !in.hasMoreTokens()) {
            in = new StringTokenizer(br.readLine());
        }
        return in.nextToken();
    }

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        try {
            br = new BufferedReader(new FileReader("line.in"));
            solve();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void solve() throws IOException {
        String inputName = nextToken();
        File inputFile = new File(inputName);
        BufferedImage inputImage = ImageIO.read(inputFile);
        String filter = nextToken();
        String outName = nextToken();
        BufferedImage outputImage;
        String format = "jpg";

        if (filter.equals("negative")) {
            outputImage = negative(inputImage);
        } else if (filter.equals("shapes")) {
            outputImage = draw(inputImage);
        } else if (filter.equals("sepia")) {
            outputImage = sepia(inputImage);
        } else if (filter.equals("colourOfMood")) {
            outputImage = mood(inputImage);
        } else if (filter.equals("grey")) {
            outputImage = grey(inputImage);
        } else if (filter.equals("blackWhite")) {
            outputImage = blackWhite(inputImage);
        } else if (filter.equals("noise")) {
            outputImage = noise(inputImage, 1);
        } else if (filter.equals("bright")) {
            outputImage = bright(inputImage, 2);
        } else if (filter.equals("dark")) {
            outputImage = bright(inputImage, 0.5);
        } else if (filter.equals("sepiaBlue")) {
            outputImage = sepiaBlue(inputImage, 10);
        } else if (filter.equals("blue")) {
            outputImage = blue(inputImage);
        } else if (filter.equals("green")) {
            outputImage = green(inputImage);
        } else {
            outputImage = inputImage;
        }
        File outputFile = new File(outName);
        ImageIO.write(outputImage, format, outputFile);
    }

    public static BufferedImage draw(BufferedImage defaultImage) {
        BufferedImage drawImage = grey(defaultImage);
        int[] gisto = new int[256];
        for (int i = 0; i < gisto.length; i++) {
            gisto[i] = 0;
        }
        for (int i = 0; i < drawImage.getWidth(); i++) {
            for (int j = 0; j < drawImage.getHeight(); j++) {
                gisto[drawImage.getRGB(i, j) << 24 >>> 24]++;
            }
        }
        int pali = 9;
        int[] d = new int[pali];
        for (int i = 0; i < pali; i++) {
            d[i] = 0;
        }
        int temp = 0;
        for (int i = 0; i < pali; i++) {
            if (i != 0) {
                d[i] = d[i - 1];
            }
            while (temp < drawImage.getHeight() * drawImage.getWidth() * (i + 1) / (pali + 1)) {
                temp += gisto[d[i]++];
            }
        }
        for (int i = 0; i < drawImage.getWidth(); i++) {
            for (int j = 0; j < drawImage.getHeight(); j++) {
                int p = (drawImage.getRGB(i, j) << 24 >>> 24);
                int abc = 0;
                while ((abc < pali) && (p > d[abc])) {
                    abc++;
                }
                p = (int) Math.pow(2, abc) - 1;
                p = (p << 24 >>> 8) + (p << 24 >>> 16) + (p << 24 >>> 24);
                drawImage.setRGB(i, j, p);
            }
        }
        return drawImage;
    }

    public static BufferedImage blackWhite(BufferedImage defaultImage) {
        BufferedImage blackWhiteImage = grey(defaultImage);
        int[] gisto = new int[256];
        for (int i = 0; i < gisto.length; i++) {
            gisto[i] = 0;
        }
        for (int i = 0; i < blackWhiteImage.getWidth(); i++) {
            for (int j = 0; j < blackWhiteImage.getHeight(); j++) {
                gisto[blackWhiteImage.getRGB(i, j) << 24 >>> 24]++;
            }
        }
        int med = 0;
        int temp = 0;
        while (temp < blackWhiteImage.getHeight() * blackWhiteImage.getWidth() / 2) {
            temp += gisto[med++];
        }
        for (int i = 0; i < blackWhiteImage.getWidth(); i++) {
            for (int j = 0; j < blackWhiteImage.getHeight(); j++) {
                if ((blackWhiteImage.getRGB(i, j) << 24 >>> 24) < med / 1.6) {
                    blackWhiteImage.setRGB(i, j, 0);
                } else {
                    blackWhiteImage.setRGB(i, j, ~0);
                }
            }
        }
        return blackWhiteImage;
    }

    public static BufferedImage negative(BufferedImage defaultImage) {
        BufferedImage negativeImage = defaultImage;
        for (int i = 0; i < defaultImage.getWidth(); i++) {
            for (int j = 0; j < defaultImage.getHeight(); j++) {
                int code = defaultImage.getRGB(i, j);
                code = ~code;
                negativeImage.setRGB(i, j, code);
            }
        }
        return negativeImage;
    }

    public static BufferedImage grey(BufferedImage defaultImage) {
        BufferedImage greyImage = defaultImage;
        for (int i = 0; i < defaultImage.getWidth(); i++) {
            for (int j = 0; j < defaultImage.getHeight(); j++) {
                int code = defaultImage.getRGB(i, j);
                int[] oldRgb = new int[3];
                for (int k = 0; k < 3; k++) {
                    oldRgb[k] = code << 8 * (1 + k) >>> 24;
                }
                int grey = (int) (oldRgb[0] * 0.2126 + oldRgb[1] * 0.7125 + oldRgb[2] * 0.0722);
                code = 0;
                for (int k = 0; k < 3; k++) {
                    code += (grey > 255 ? 255 : (grey < 0 ? 0 : grey)) << 8 * (2 - k);
                }
                greyImage.setRGB(i, j, code);
            }
        }
        return greyImage;
    }

    public static BufferedImage noise(BufferedImage defaultImage, double n) {
        BufferedImage noiseImage = defaultImage;
        for (int i = 0; i < defaultImage.getWidth(); i++) {
            for (int j = 0; j < defaultImage.getHeight(); j++) {
                int code = defaultImage.getRGB(i, j);
                int[] oldRgb = new int[3];
                for (int k = 0; k < 3; k++) {
                    oldRgb[k] = code << 8 * (1 + k) >>> 24;
                }
                int[] newRgb = new int[3];
                for (int k = 0; k < 3; k++) {
                    double br = (Math.random() - 0.5) * n;
                    newRgb[k] = (int) (oldRgb[k] * (1 + br));
                }
                code = 0;
                for (int k = 0; k < 3; k++) {
                    code += (newRgb[k] > 255 ? 255 : (newRgb[k] < 0 ? 0 : newRgb[k])) << 8 * (2 - k);
                }
                noiseImage.setRGB(i, j, code);
            }
        }
        return noiseImage;
    }

    public static BufferedImage bright(BufferedImage defaultImage, double br) {
        BufferedImage brightImage = defaultImage;
        for (int i = 0; i < defaultImage.getWidth(); i++) {
            for (int j = 0; j < defaultImage.getHeight(); j++) {
                int code = defaultImage.getRGB(i, j);
                int[] oldRgb = new int[3];
                for (int k = 0; k < 3; k++) {
                    oldRgb[k] = code << 8 * (1 + k) >>> 24;
                }
                int[] newRgb = new int[3];
                for (int k = 0; k < 3; k++) {
                    newRgb[k] = (int) (oldRgb[k] * br);
                }
                code = 0;
                for (int k = 0; k < 3; k++) {
                    code += (newRgb[k] > 255 ? 255 : (newRgb[k] < 0 ? 0 : newRgb[k])) << 8 * (2 - k);
                }
                brightImage.setRGB(i, j, code);
            }
        }
        return brightImage;
    }

    public static BufferedImage sepiaBlue(BufferedImage defaultImage, int depth) {
        BufferedImage sepiaBlueImage = defaultImage;
        for (int i = 0; i < defaultImage.getWidth(); i++) {
            for (int j = 0; j < defaultImage.getHeight(); j++) {
                int code = defaultImage.getRGB(i, j);
                int[] oldRgb = new int[3];
                for (int k = 0; k < 3; k++) {
                    oldRgb[k] = code << 8 * (1 + k) >>> 24;
                }
                double aver = (double) (oldRgb[0] + oldRgb[1] + oldRgb[2]) / 3;
                int[] newRgb = new int[3];
                newRgb[2] = (int) (aver + (174 * depth / 50));
                newRgb[1] = (int) (aver + depth);
                newRgb[0] = (int) (aver);
                code = 0;
                for (int k = 0; k < 3; k++) {
                    code += (newRgb[k] > 255 ? 255 : newRgb[k]) << 8 * (2 - k);
                }
                sepiaBlueImage.setRGB(i, j, code);
            }
        }
        return sepiaBlueImage;
    }

    public static BufferedImage sepia(BufferedImage defaultImage) {
        BufferedImage sepiaImage = defaultImage;
        for (int i = 0; i < defaultImage.getWidth(); i++) {
            for (int j = 0; j < defaultImage.getHeight(); j++) {
                int code = defaultImage.getRGB(i, j);
                int[] oldRgb = new int[3];
                for (int k = 0; k < 3; k++) {
                    oldRgb[k] = code << 8 * (1 + k) >>> 24;
                }
                int[] newRgb = new int[3];
                newRgb[0] = (int) (oldRgb[0] * 0.393 + oldRgb[1] * 0.769 + oldRgb[2] * 0.189);
                newRgb[1] = (int) (oldRgb[0] * 0.349 + oldRgb[1] * 0.686 + oldRgb[2] * 0.168);
                newRgb[2] = (int) (oldRgb[0] * 0.272 + oldRgb[1] * 0.534 + oldRgb[2] * 0.131);
                code = 0;
                for (int k = 0; k < 3; k++) {
                    code += (newRgb[k] > 255 ? 255 : newRgb[k]) << 8 * (2 - k);
                }
                sepiaImage.setRGB(i, j, code);
            }
        }
        return sepiaImage;
    }

    public static BufferedImage mood(BufferedImage defaultImage) {
        BufferedImage moodImage = defaultImage;
        for (int i = 0; i < defaultImage.getWidth(); i++) {
            for (int j = 0; j < defaultImage.getHeight(); j++) {
                int code = defaultImage.getRGB(i, j);
                int[] oldRgb = new int[3];
                for (int k = 0; k < 3; k++) {
                    oldRgb[k] = code << 8 * (1 + k) >>> 24;
                }
                int[] newRgb = new int[3];
                newRgb[2] = (int) (oldRgb[0] * 0.2126 + oldRgb[1] * 0.7125 + oldRgb[2] * 0.0722);
                newRgb[1] = (int) (oldRgb[0] * -0.0999 + oldRgb[1] * -0.3360 + oldRgb[2] * 0.4360);
                newRgb[0] = (int) (oldRgb[0] * 0.6150 + oldRgb[1] * -0.5586 + oldRgb[2] * -0.0563);
                code = 0;
                for (int k = 0; k < 3; k++) {
                    code += (newRgb[k] > 255 ? 255 : (newRgb[k] < 0 ? 0 : newRgb[k])) << 8 * (2 - k);
                }
                moodImage.setRGB(i, j, code);
            }
        }
        return moodImage;
    }

    public static BufferedImage blue(BufferedImage defaultImage) {
        BufferedImage blueImage = defaultImage;
        for (int i = 0; i < defaultImage.getWidth(); i++) {
            for (int j = 0; j < defaultImage.getHeight(); j++) {
                int code = defaultImage.getRGB(i, j);
                int[] oldRgb = new int[3];
                oldRgb[0] = 0;
                oldRgb[1] = 0;
                oldRgb[2] = code << 24 >>> 24;

                code = 0;
                for (int k = 0; k < 3; k++) {
                    code += (oldRgb[k] > 255 ? 255 : (oldRgb[k] < 0 ? 0 : oldRgb[k])) << 8 * (2 - k);
                }
                blueImage.setRGB(i, j, code);
            }
        }
        return blueImage;
    }

    public static BufferedImage green(BufferedImage defaultImage) {
        BufferedImage greenImage = defaultImage;
        for (int i = 0; i < defaultImage.getWidth(); i++) {
            for (int j = 0; j < defaultImage.getHeight(); j++) {
                int code = defaultImage.getRGB(i, j);
                int[] oldRgb = new int[3];
                oldRgb[0] = 0;
                oldRgb[1] = code << 16 >>> 24;
                oldRgb[2] = 0;

                code = 0;
                for (int k = 0; k < 3; k++) {
                    code += (oldRgb[k] > 255 ? 255 : (oldRgb[k] < 0 ? 0 : oldRgb[k])) << 8 * (2 - k);
                }
                greenImage.setRGB(i, j, code);
            }
        }
        return greenImage;
    }
}