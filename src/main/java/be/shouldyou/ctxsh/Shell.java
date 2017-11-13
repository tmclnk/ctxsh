package be.shouldyou.ctxsh;

import be.shouldyou.example.MyConfig;

/**
 * Launches a shell with a Spring ApplicationContext in a terminal.
 */
public class Shell {
	public static void main(String[] args) throws Exception {
		int ret;
		try (GroovyShell shell = new GroovyShell(MyConfig.class)){
			ret = shell.eval(System.in);
		}
		System.exit(ret);
	}
}
