package com.shinwell.mda.util;

import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class SshClient implements Closeable {
	public static void main(String[] args) {
		try {
			// SshClient.newInstance().shell("porterrshow", "ssh.log");
			SshClient.newInstance().shell("porterrshow");
			// SshClient.newInstance().shell("source .profile | porterrshow");
			// SshClient.newInstance().exec("source .profile | porterrshow");
			SshClient.newInstance().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static long interval = 1000L;
	static int timeout = 3000;
	private SshInfo sshInfo = null;
	private JSch jsch = null;
	private Session session = null;

	private SshClient(SshInfo info) throws Exception {
		sshInfo = info;
		jsch = new JSch();
		// jsch.addIdentity( sshInfo.getKey() );
		session = jsch.getSession(sshInfo.getUser(), sshInfo.getHost(), sshInfo.getPort());
		UserInfo ui = new SshUserInfo(sshInfo.getPassPhrase());
		session.setUserInfo(ui);
		Properties config = new Properties();
		config.setProperty("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();
	}

	public long shell(String cmd, String outputFileName) throws Exception {
		long start = System.currentTimeMillis();
		ChannelShell channelShell = (ChannelShell) session.openChannel("shell");
		PipedInputStream pipeIn = new PipedInputStream();
		PipedOutputStream pipeOut = new PipedOutputStream(pipeIn);
		FileOutputStream fileOut = new FileOutputStream(outputFileName);
		channelShell.setInputStream(pipeIn);
		channelShell.setOutputStream(fileOut);
		channelShell.connect(timeout);
		pipeOut.write(cmd.getBytes());
		Thread.sleep(interval);
		pipeOut.close();
		pipeIn.close();
		fileOut.close();
		channelShell.disconnect();
		return System.currentTimeMillis() - start;
	}

	public List<String> shell(String script) throws Exception {
		ArrayList<String> strs = new ArrayList<String>();
		ChannelShell channelShell = (ChannelShell) session.openChannel("shell");
		channelShell.connect();
		InputStream inputStream = channelShell.getInputStream();
		OutputStream outputStream = channelShell.getOutputStream();
		PrintWriter printWriter = new PrintWriter(outputStream);
		printWriter.println(script);
		//printWriter.println("exit");// 加上个就是为了，结束本次交互
		printWriter.flush();
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		String msg = null;
		while ((msg = in.readLine()) != null) {
			System.out.println(msg);
			strs.add(msg);
		}
		in.close();
		printWriter.close();
		channelShell.disconnect();
		return strs;
	}

	public int exec(String cmd) throws Exception {
		ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
		channelExec.setCommand(cmd);
		channelExec.setInputStream(null);
		channelExec.setErrStream(System.err);
		InputStream in = channelExec.getInputStream();
		channelExec.connect();
		int res = -1;
		StringBuffer buf = new StringBuffer(1024);
		byte[] tmp = new byte[1024];
		while (true) {
			while (in.available() > 0) {
				int i = in.read(tmp, 0, 1024);
				if (i < 0)
					break;
				buf.append(new String(tmp, 0, i));
			}
			if (channelExec.isClosed()) {
				res = channelExec.getExitStatus();
				System.out.println(format("Exit-status: %d", res));
				break;
			}
		}
		System.out.println(buf.toString());
		channelExec.disconnect();
		return res;
	}

	public static SshClient newInstance() throws Exception {
		String host = "10.10.0.16";
		Integer port = 22;
		String user = "root";
		String key = "./id_dsa";
		String passPhrase = "password";
		SshInfo i = new SshInfo(host, port, user, key, passPhrase);
		return new SshClient(i);
	}

	public Session getSession() {
		return session;
	}

	public void close() throws IOException {
		getSession().disconnect();
	}
}

class SshInfo {
	String host = null;
	Integer port = 22;
	String user = null;
	String key = null;
	String passPhrase = null;

	public SshInfo(String host, Integer port, String user, String key, String passPhrase) {
		super();
		this.host = host;
		this.port = port;
		this.user = user;
		this.key = key;
		this.passPhrase = passPhrase;
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

	public String getKey() {
		return key;
	}

	public String getPassPhrase() {
		return passPhrase;
	}

}

class SshUserInfo implements UserInfo {

	private String passphrase = null;

	public SshUserInfo(String passphrase) {
		super();
		this.passphrase = passphrase;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public String getPassword() {
		return passphrase;
	}

	public boolean promptPassphrase(String pass) {
		return true;
	}

	public boolean promptPassword(String pass) {
		return true;
	}

	public boolean promptYesNo(String arg0) {
		return true;
	}

	public void showMessage(String m) {
		System.out.println(m);
	}

}