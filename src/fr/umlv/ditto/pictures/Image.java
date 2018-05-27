package fr.umlv.ditto.pictures;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.Color;

import javax.imageio.ImageIO;

public class Image {
	private final BufferedImage image;
	private final int width;
	private final int height;
	final static String outType = "png";

	private Image(BufferedImage image, int weigth, int height) {
		this.image = image;
		this.width = weigth;
		this.height = height;
	}

	public static Image newImage(BufferedImage image) {
		return new Image(image, image.getWidth(), image.getHeight());
	}

	public static Image newImage(Path file) throws IOException {
		try (InputStream is = Files.newInputStream(file)) {
			BufferedImage image = ImageIO.read(is);
			return newImage(image);
		}
	}

	public static Image newImage(int width, int height) {
		return newImage(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB));
	}

	public void printToFile(String file) throws IOException {
		Path path = Paths.get(file + "." + outType);
		try (OutputStream out = Files.newOutputStream(path)) {
			ImageIO.write(image, outType, out);
			System.out.println("Output image written in " + file);
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getSize() {
		return width * height;
	}

	public Color get(int x, int y) {
		return new Color(image.getRGB(x, y));
	}

	public int getRed(int x, int y) {
		return new Color(image.getRGB(x, y)).getRed();
	}

	public int getBlue(int x, int y) {
		return new Color(image.getRGB(x, y)).getBlue();
	}

	public int getGreen(int x, int y) {
		return new Color(image.getRGB(x, y)).getGreen();
	}

	public int getGrayValue(int x, int y) {
		Color C = new Color(image.getRGB(x, y));
		return (C.getBlue() + C.getRed() + C.getGreen()) / 3;
	}

	public Color get(int u) {
		return get(u / height, u % height);
	}

	public int getRed(int u) {
		return getRed(u / height, u % height);
	}

	public int getBlue(int u) {
		return getBlue(u / height, u % height);
	}

	public int getGreen(int u) {
		return getGreen(u / height, u % height);
	}

	public int getGrayValue(int u) {
		return getGrayValue(u / height, u % height);
	}

	public void set(int x, int y, Color c) {
		image.setRGB(x, y, c.getRGB());
	}

	public void setRed(int x, int y, int value) {
		Color c = new Color(image.getRGB(x, y));
		image.setRGB(x, y, new Color(range(value), c.getGreen(), c.getBlue()).getRGB());
	}

	public void setGreen(int x, int y, int value) {
		Color c = new Color(image.getRGB(x, y));
		image.setRGB(x, y, new Color(c.getRed(), range(value), c.getBlue()).getRGB());
	}

	public void setBlue(int x, int y, int value) {
		Color c = new Color(image.getRGB(x, y));
		image.setRGB(x, y, new Color(c.getRed(), c.getGreen(), range(value)).getRGB());
	}

	public void set(int u, Color c) {
		set(u / height, u % height, c);
	}

	public void setRed(int u, int value) {
		setRed(u / height, u % height, value);
	}

	public void setGreen(int u, int value) {
		setGreen(u / height, u % height, value);
	}

	public void setBlue(int u, int value) {
		setBlue(u / height, u % height, value);
	}

	private int range(int value) {
		return value < 0 ? 0 : (value > 255 ? 255 : value);
	}

	public static double capacity(int rgb1start, int rgb2start, int rgb1end, int rgb2end) {
		double diffStart = delta(rgb1start, rgb2start);
		double diffEnd = delta(rgb1end, rgb2end);
		double g1 = delta(rgb1start, rgb1end);
		double g2 = delta(rgb2start, rgb2end);
		double weight = (diffStart + diffEnd) / (g1 + g2 + 0.0001);
		return Math.max(weight, 0);
	}

	private static double delta(int rgb1, int rgb2) {
		Color c1 = new Color(rgb1);
		Color c2 = new Color(rgb2);
		int red = c1.getRed() - c2.getRed();
		int green = c1.getGreen() - c2.getGreen();
		int blue = c1.getBlue() - c2.getBlue();
		return Math.sqrt((red * red + green * green + blue * blue + 0.0) / 3.0) / 255.0;
	}

	public static int blur(int rgb1, int rgb2, double cut) {
		Color c1 = new Color(rgb1);
		Color c2 = new Color(rgb2);
		int red = (int) (cut * c1.getRed() + (1 - cut) * c2.getRed());
		int green = (int) (cut * c1.getGreen() + (1 - cut) * c2.getGreen());
		int blue = (int) (cut * c1.getBlue() + (1 - cut) * c2.getBlue());
		return new Color(red, green, blue).getRGB();
	}
}
