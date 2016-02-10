import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContext {
	private static SpringContext install =new SpringContext();
	public static SpringContext getInstace() {
		return install;
	}
	private ApplicationContext ctx = null;
	private SpringContext() {
		String path1="spring.xml";
		this.ctx = new ClassPathXmlApplicationContext(new String[]{path1});
	}
	@SuppressWarnings("unchecked")
	public <T> T getBean(String arg) {
		return (T)this.ctx.getBean(arg);
	}
	public <T> T getBean(Class<T> t) {
		return (T)this.ctx.getBean(t);
	}
	public ApplicationContext getCtx() {
		return this.ctx;
	}
	public static <T> T getTBean(Class<T> t) {	
		return SpringContext.getInstace().getBean(t);
	}
}
