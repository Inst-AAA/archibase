package helper;

public class ColorHelper {
    public static int RED = 0xff1111;
    public static int YELLOW = 0xFFFF00;
    public static int BLUE = 0x0fa2ef;
    public static int PINK = 0xff66cc;
    public static int LIGHTRED = 0xF3CBC3;
    public static int LIGHTBLUE = 0xC3DBF3;
    public static int BACKGROUNDBLUE = 0x1e2832;


    public static int[] hexToRGB(int c) {
        int[] ret = new int[3];
        for (int i = 2; i >= 0; --i) {
            ret[i] = c & (0xff);
            c >>= 8;
        }
        return ret;
    }

    public static int rgbToHEX(int[] rgb) {
        int c = 0;
        for (int i = 0; i < 3; ++i) {
            c ^= rgb[i];
            if (i != 2)
                c <<= 8;
        }
        return c;
    }

    public static int rgbToHEX(int r, int g, int b) {
        return rgbToHEX(new int[]{r, g, b});
    }

    public static float[] rgbToHSL(int r, int g, int b) {
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float h = 0.0f, s = 0.0f, l = 0.0f;

        // hue
        if (max == min)
            h = 0;
        if (max == r && g >= b)
            h = 60.0f * (g - b) / (max - min);
        if (max == r && g < b)
            h = 60.0f * (g - b) / (max - min) + 360;
        if (max == g)
            h = 60.0f * (b - r) / (max - min) + 120;
        if (max == b)
            h = 60.0f * (r - g) / (max - min) + 240;

        // lightness
        l = 0.5f * (max + min);

        // saturation
        if (l == 0 || max == min)
            s = 0;
        if (l > 0 && l <= 0.5)
            s = (max - min) / (2.0f * l);
        if (l > 0.5)
            s = (max - min) / (2.0f - 2.0f * l);

        return new float[]{h, s, l};
    }

    public static int hsvToHEX(float[] hsv) {
        int[] rgb = hsvToRGB(hsv);
        return rgbToHEX(rgb);
    }

    public static int hsvToHEX(float h, float s, float v) {
        int[] rgb = hsvToRGB(h, s, v);
        System.out.println(rgb[0] + " " + rgb[1] + " " + rgb[2]);
        return rgbToHEX(rgb);
    }

    public static float[] hexToHSV(int c) {
        int[] rgb = hexToRGB(c);
        return rgbToHSV(rgb);
    }

    public static float[] rgbToHSV(int r, int g, int b) {
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float h = 0.0f, s = 0.0f, v = 0.0f;


        // hue
        if (max == min)
            h = 0.0f;
        if (max == r && g >= b)
            h = 60.0f * (g - b) / (max - min);
        if (max == r && g < b)
            h = 60.0f * (g - b) / (max - min) + 360;
        if (max == g)
            h = 60.0f * (b - r) / (max - min) + 120;
        if (max == b)
            h = 60.0f * (r - g) / (max - min) + 240;

        // saturation
        if (max == 0)
            s = 0;
        else
            s = 1.0f - min / max;

        // brightness
        v = max;

        return new float[]{h / 360.0f * 255.0f, s * 255.0f, v};
    }

    public static float[] rgbToHSV(int[] c) {
        return rgbToHSV(c[0], c[1], c[2]);
    }

    public static int[] hsvToRGB(float h, float s, float v) {
        h *= 360.0f / 255.0f;
        s /= 255.0f;
        v /= 255.0f;

        int flag = ((int) Math.floor(h / 60.0f)) % 6;
        float f = h / 60.0f - flag;
        float p = v * (1 - s);
        float q = v * (1 - f * s);
        float t = v * (1 - (1 - f) * s);

        float r = 0, g = 0, b = 0;
        switch (flag) {
            case 0:
                r = v;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = v;
                b = p;
                break;
            case 2:
                r = p;
                g = v;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = v;
                break;
            case 4:
                r = t;
                g = p;
                b = v;
                break;
            case 5:
                r = v;
                g = p;
                b = q;
                break;
        }
        return new int[]{(int) Math.round(255 * r + Tools.EPS), (int) Math.round(255 * g + Tools.EPS), (int) Math.round(255 * b + Tools.EPS)};
    }

    public static int[] hsvToRGB(float[] cc) {
        return hsvToRGB(cc[0], cc[1], cc[2]);
    }

    public static int[][] createGradientHue(int num, float[] a, float[] b) {
        float min = Math.min(a[0], b[0]);
        float max = Math.max(a[0], b[0]);
        float s = Math.min(a[1], b[1]);
        float v = Math.max(a[2], b[2]);

        int[][] c = new int[num][3];
        float step = (max - min) / (num - 1);
        for (int i = 0; i < num; ++i) {
            float h = min + i * step;
            c[i] = hsvToRGB(h, s, v);
        }
        return c;
    }

    public static int[][] createGradientHue(int num, int a, int b) {
        return createGradientHue(num, hexToHSV(a), hexToHSV(b));
    }


    public static int[][] createGradientBright(int num, float[] a) {

        float h = a[0];
        float s = a[1];

        int[][] c = new int[num][3];
        float step = a[2] / (num - 1);
        for (int i = 0; i < num; ++i) {
            float v = step * i;
            c[i] = hsvToRGB(h, s, v);
        }
        return c;
    }

    public static int[][] createGradientBright(int num, int a) {
        return createGradientBright(num, hexToHSV(a));

    }

    public static int[][] createGradientSaturate(int num, float[] a) {

        float h = a[0];
        float v = a[2];

        int[][] c = new int[num][3];
        float step = (a[1] - 80) / (num - 1);
        for (int i = 0; i < num; ++i) {
            float s = step * i + 80;
            c[i] = hsvToRGB(h, s, v);
        }
        return c;
    }

    public static int[][] createGradientSaturate(int num, int a) {
        return createGradientSaturate(num, hexToHSV(a));
    }


    public static int[] colorLighter(int c, double ratio) {
        float[] hsv = hexToHSV(c);
        hsv[2] *= (1 + ratio);
        if (hsv[2] > 255) hsv[2] = 255.0f;
        return hsvToRGB(hsv);
    }
}