import javax.jdo.*;
import javax.jdo.spi.PersistenceCapable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * A demonstration of serialization using JDO.
 * JDO is normally used to access a database, but can also be used
 * to save locally, which is shown here.
 */
public class SerialDemoJDO extends SerialDemoAbstractBase {

	public static void main(String[] args) throws IOException {
		SerialDemoJDO jd = new SerialDemoJDO();
		jd.save();
		jd.dump();
	}

	public PersistenceManager getPM() {
		Properties p = new Properties();
		try {
			p.load(new FileInputStream("jdo.properties"));
			PersistenceManagerFactory pmf = 
				JDOHelper.getPersistenceManagerFactory(p);
			return pmf.getPersistenceManager();
		} catch (IOException ex) {
			throw new RuntimeException(ex.toString());
		}
	}

	public void write(Object o) {
		if (o instanceof Collection){
			Iterator it = (((Collection)o).iterator());
			while (it.hasNext()) {
				write(it.next());
			}
		}
		if (!(o instanceof PersistenceCapable)) {
			throw new IllegalArgumentException(
				"Data class " + o.getClass().getName() +
				" has not been JDO-enhanced");
		}
		PersistenceManager pm = getPM();
		pm.currentTransaction().begin();
		pm.makePersistent(o);
		pm.currentTransaction().commit();
		pm.close();
	}

	public void dump() {
		PersistenceManager pm = getPM();
		Object[] data = new Object[3];
		pm.retrieveAll(data);
		for (int i = 0; i < data.length; i++) {
			System.out.println(data[i]);
		}
		pm.close();
	}
}
