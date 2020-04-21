package ummisco.gama.runner;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Runner {

	public void extractZip(String path) {
//		String executionPath = System.getProperty("user.dir");
		File currentJavaJarFile = new File(Runner.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String currentJavaJarFilePath = currentJavaJarFile.getAbsolutePath();
		String executionPath = currentJavaJarFilePath.replace(currentJavaJarFile.getName(), "");
		File file1 = new File(executionPath + "/Gama1.7-linux.gtk.x86_64_withJDK_COMOKIT.zip");
		if (!file1.exists()) {
			InputStream link = (getClass().getResourceAsStream("/Gama1.7-linux.gtk.x86_64_withJDK_COMOKIT.zip"));
			try {
				Files.copy(link, file1.getAbsoluteFile().toPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			unzip(executionPath + "/Gama1.7-linux.gtk.x86_64_withJDK_COMOKIT.zip", path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		// Open the file
//		try (ZipFile file = new ZipFile(file1)) {
//			FileSystem fileSystem = FileSystems.getDefault();
//			// Get file entries
//			Enumeration<? extends ZipEntry> entries = file.entries();
//
//			// We will unzip files in this folder
//			String uncompressedDirectory = "uncompressed/";
//			if (!new File(uncompressedDirectory).exists())
//				Files.createDirectory(fileSystem.getPath(uncompressedDirectory));
//
//			// Iterate over entries
//			while (entries.hasMoreElements()) {
//				ZipEntry entry = entries.nextElement();
//				// If directory then create a new directory in uncompressed folder
//				if (entry.isDirectory()) {
//					if (!new File(uncompressedDirectory + entry.getName()).exists())
////							new File(uncompressedDirectory + entry.getName()).mkdirs();
////                    System.out.println("Creating Directory:" + uncompressedDirectory + entry.getName());
//						Files.createDirectories(fileSystem.getPath(uncompressedDirectory + entry.getName()));
//				}
//				// Else create the file
//				else {
//					InputStream is = file.getInputStream(entry);
//					BufferedInputStream bis = new BufferedInputStream(is);
//					String uncompressedFileName = uncompressedDirectory + entry.getName();
//					Path uncompressedFilePath = fileSystem.getPath(uncompressedFileName);
//					Files.createFile(uncompressedFilePath);
//					FileOutputStream fileOutput = new FileOutputStream(uncompressedFileName);
//					while (bis.available() > 0) {
//						fileOutput.write(bis.read());
//					}
//					fileOutput.close();
////                    System.out.println("Written :" + entry.getName());
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	public static void unzip(String zipFile, String location) throws IOException {
		int BUFFER_SIZE = 4096;
		int size;
		byte[] buffer = new byte[BUFFER_SIZE];

		try {
			if (!location.endsWith(File.separator)) {
				location += File.separator;
			}
			File f = new File(location);
			if (!f.isDirectory()) {
				f.mkdirs();
			}
			ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));
			try {
				ZipEntry ze = null;
				while ((ze = zin.getNextEntry()) != null) {
					String path = location + ze.getName();
					File unzipFile = new File(path);

					if (ze.isDirectory()) {
						if (!unzipFile.isDirectory()) {
							unzipFile.mkdirs();
						}
					} else {
						// check for and create parent directories if they don't exist
						File parentDir = unzipFile.getParentFile();
						if (null != parentDir) {
							if (!parentDir.isDirectory()) {
								parentDir.mkdirs();
							}
						}

						// unzip the file
						FileOutputStream out = new FileOutputStream(unzipFile, false);
						BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
						try {
							while ((size = zin.read(buffer, 0, BUFFER_SIZE)) != -1) {
								fout.write(buffer, 0, size);
							}

							zin.closeEntry();
						} finally {
							fout.flush();
							fout.close();
						}
					}
				}
			} finally {
				zin.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String console(final String s, final String directory) {
		String LN = java.lang.System.getProperty("line.separator");
		if (s == null || s.isEmpty()) {
			return "";
		}
		final StringBuilder output = new StringBuilder();
		final List<String> commands = new ArrayList<>();
		String os = System.getProperty("os.name");
		commands.add(os.startsWith("Win") ? "cmd.exe" : "/bin/bash");
		commands.add(os.startsWith("Win") ? "/C" : "-c");

//		commands.add("bash");
//		commands.add("-c");
		commands.add(s.trim());
		// commands.addAll(Arrays.asList(s.split(" ")));
		final boolean nonBlocking = commands.get(commands.size() - 1).endsWith("&");
		if (nonBlocking) {
			// commands.(commands.size() - 1);
		}
		final ProcessBuilder b = new ProcessBuilder(commands);
		b.redirectErrorStream(true);
		b.directory(new File(directory));
		try {
			final Process p = b.start();
			if (nonBlocking) {
				return "";
			}
			final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//			final int returnValue = p.waitFor();
			String line = "";
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}

		} catch (final Exception e) {
			e.printStackTrace();
		}
		return output.toString();

	}

	public void run(String path, String[] args) {
//		String out=console("/Gama/headless/gama-headless.sh samples/predatorPrey.xml o", "/");
		String os = System.getProperty("os.name");

//		String fscript = os.startsWith("Win") ? path + "\\headless\\gama-headless.bat" : path + "/headless/gama-headless.sh";
		String fscript = path + "/headless/gama-headless.sh";
		String passWork = ".work" + Math.random() * 1000;
		fscript = "java -cp \"" + path
				+ "/plugins/org.eclipse.equinox.launcher_1.5.300.v20190213-1655.jar\" -Xms512m -Xmx4096m "
				+ "-Djava.awt.headless=true org.eclipse.core.launcher.Main  -application msi.gama.headless.id4 -data "
				+ passWork + " \"" + args[1] + "\" \"" + args[2] + "\"";

		console(fscript + " \"" + args[1] + "\" \"" + args[2] + "\"", path + "/headless");
//			Runtime r=Runtime.getRuntime();
//		r.exec("java -jar trang.jar 5-something.xml 5.1-somethingElse.xsd");
	}

	public static void main(String[] args) {
		System.out.println("hello ");

		Runner r = new Runner();
		if ((args.length) == 3) {
			r.extractZip(args[0]);
			r.run(args[0], args);
		}

	}
}
