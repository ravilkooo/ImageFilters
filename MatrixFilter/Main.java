import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
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
            br = new BufferedReader(new FileReader("matrix.in"));
            solve();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void solve() throws IOException {
        String filename = nextToken();
        String name = nextToken();
        String power = nextToken();
        int size;
        if (power.equals("low")) {
            size = 3;
        } else if (power.equals("strong")) {
            size = 10;
        } else if (power.equals("very_strong")) {
            size = 15;
        } else {
            size = 5;
        }
        String filenameOut = nextToken();
        File input = new File(filename);
        BufferedImage inImage = ImageIO.read(input);
        BufferedImage outImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        filter(inImage, outImage, name, size);
        File f = new File(filenameOut);
        String format = "jpg";
        ImageIO.write(outImage, format, f);
    }

    public void filter(BufferedImage inImage, BufferedImage outImage, String name, int power) {
        power = power - (power % 2) + 1;
        int half = power / 2;
        BufferedImage tempImage = new BufferedImage(inImage.getWidth() + half * 2, inImage.getHeight() + half * 2, BufferedImage.TYPE_INT_RGB);
        makeTemp(inImage, tempImage, half);
        if (name.equals("smooth")) {
            doSmoothFilt(tempImage, outImage, half);
        } else {
            double[][] matrix = new double[power][power];
            makeMatrix(matrix, name);
            doFilt(tempImage, outImage, matrix, half);
        }
    }

    public void doSmoothFilt(BufferedImage tempImage, BufferedImage outImage, int half) {
        for (int i = 0; i < outImage.getWidth(); i++) {
            for (int j = 0; j < outImage.getHeight(); j++) {
                int[] cols = new int[(half * 2 + 1) * (half * 2 + 1)];

                for (int k = 0; k < half * 2 + 1; k++) {
                    for (int l = 0; l < half * 2 + 1; l++) {
                        int rgb = tempImage.getRGB(i + k, j + l) << 8 >>> 8;
                        cols[k * (half * 2 + 1) + l] = rgb;
                    }
                }
                Arrays.sort(cols);
                outImage.setRGB(i, j, cols[cols.length / 2]);
            }
        }
    }

    public void doFilt(BufferedImage tempImage, BufferedImage outImage, double[][] matrix, int half) {
        for (int i = 0; i < outImage.getWidth(); i++) {
            for (int j = 0; j < outImage.getHeight(); j++) {
                double r = 0;
                double g = 0;
                double b = 0;
                for (int k = 0; k < half * 2 + 1; k++) {
                    for (int l = 0; l < half * 2 + 1; l++) {
                        double rt = tempImage.getRGB(i + k, j + l) << 8 >>> 24;
                        double gt = tempImage.getRGB(i + k, j + l) << 16 >>> 24;
                        double bt = tempImage.getRGB(i + k, j + l) << 24 >>> 24;
                        r = r + rt * matrix[k][l];
                        g = g + gt * matrix[k][l];
                        b = b + bt * matrix[k][l];
                    }
                }
                r = r > 255 ? 255 : r;
                g = g > 255 ? 255 : g;
                b = b > 255 ? 255 : b;
                r = r < 0 ? 0 : r;
                g = g < 0 ? 0 : g;
                b = b < 0 ? 0 : b;
                int col = (((int) r) << 16) + (((int) g) << 8) + ((int) b);
                outImage.setRGB(i, j, col);
            }
        }
    }

    public void makeTemp(BufferedImage inImage, BufferedImage tempImage, int half) {
        for (int i = 0; i < inImage.getWidth(); i++) {
            for (int j = 0; j < inImage.getHeight(); j++) {
                int col = inImage.getRGB(i, j);
                tempImage.setRGB(i + half, j + half, col);
            }
        }
        for (int i = 0; i < inImage.getHeight(); i++) { //left and right
            for (int j = 0; j < half; j++) {
                int col = inImage.getRGB(half - 1 - j, i);
                tempImage.setRGB(j, i + half, col);
                col = inImage.getRGB(inImage.getWidth() - 1 - j, i);
                tempImage.setRGB(tempImage.getWidth() - half + j, i + half, col);
            }
        }
        for (int i = 0; i < inImage.getWidth(); i++) { //top and bottom
            for (int j = 0; j < half; j++) {
                int col = inImage.getRGB(i, half - 1 - j);
                tempImage.setRGB(i + half, j, col);
                col = inImage.getRGB(i, inImage.getHeight() - 1 - j);
                tempImage.setRGB(i + half, tempImage.getHeight() + j - half, col);
            }
        }
        for (int i = 0; i < half; i++) { //angles
            for (int j = 0; j < half; j++) {
                int col = tempImage.getRGB(j, i + half);
                tempImage.setRGB(i, j, col);
                col = tempImage.getRGB(j + half, tempImage.getHeight() - 1 - i);
                tempImage.setRGB(i, tempImage.getHeight() - half + j, col);
                col = tempImage.getRGB(tempImage.getWidth() - 1 - j, i + half);
                tempImage.setRGB(tempImage.getWidth() - half + i, j, col);
                col = tempImage.getRGB(tempImage.getWidth() - half + j, tempImage.getHeight() - half - i - 1);
                tempImage.setRGB(tempImage.getWidth() - half + i, tempImage.getHeight() - half + j, col);
            }
        }
    }

    public void makeMatrix(double[][] matrix, String name) {
        if (name.equals("unblur")) {
            fillMatrixUnblur(matrix);
        } else if (name.equals("unblur2")) {
            fillMatrixUnblur2(matrix);
        } else {
            fillMatrixBlur(matrix);
        }
    }

    public void fillMatrixUnblur2(double[][] matrix) {
        double[][] unBlurMatrix = {{-1, -1, -1}, {-1, 9, -1}, {-1, -1, -1}};
        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(unBlurMatrix[i], 0, matrix[i], 0, matrix.length);
        }
    }

    public void fillMatrixBlur(double[][] matrix) {
        int si = 5;
        int size = matrix[0].length;
        double mid = (double) size / 2 + 1;
        double sum = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double x = i - mid + 1;
                double y = j - mid + 1;
                matrix[i][j] = Math.exp(-(x * x + y * y) / (2 * si * si)) / (2 * Math.PI * si * si);
                sum += matrix[i][j];
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = matrix[i][j] / sum;
            }
        }
    }

    public void fillMatrixUnblur(double[][] matrix) {
        fillMatrixBlur(matrix);
        double alpha = 2;
        int size = matrix[0].length;
        int mid = size / 2 + 1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = -alpha * matrix[i][j];
            }
        }
        matrix[mid - 1][mid - 1] = matrix[mid - 1][mid - 1] + alpha + 1;
    }
}