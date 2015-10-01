package be.chaouki.prototype;


import be.chaouki.prototype.entities.Event;
import be.chaouki.prototype.util.JPAUtil;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Code taken from this tutorial http://www.hibernatespatial.org/documentation/02-Tutorial/01-tutorial4/
 * which is a tutorial for Hibernate Spatial 4 but I adapted the code for Hibernate Spatial 5.
 *
 * Use as program arguments:
 * a) "store POINT(10 5)"
 * in order to save an event named My title with the date and time set to right now
 * and the position set to the point (10 5).
 * b) "find POLYGON((1 1,20 1,20 20,1 20,1 1))"
 * in order to query every event that happended inside the rectangular defined by the 4 points (1 1), (1 20) (20 1)
 * and (20 20).
 *
 * note: MySQL56SpatialDialect and MySQL56InnoDBSpatialDialect doesnt seem to work. However MySQL5InnoDBSpatialDialect
 * and MySQLSpatialDialect does. MySQL5InnoDBSpatialDialect is currently used.
 */
public class EventManager {

    private static Logger logger = Logger.getLogger(EventManager.class);

    public static void main(String[] args) {
        EventManager mgr = new EventManager();

        if (args[0].equals("store")) {
            mgr.createAndStoreEvent("My Event", new Date(), assemble(args));
        }else if (args[0].equals("find")) {
            List events = mgr.find(assemble(args));
            logger.info("Result of the find: --------------------------------");
            logger.info(events.size()+" result(s)");
            for (int i = 0; i < events.size(); i++) {
                Event event = (Event) events.get(i);
                System.out.println("Event: " + event.getTitle() +
                        ", Time: " + event.getDate() +
                        ", Location: " + event.getLocation());
            }
        }

        JPAUtil.close();
    }

    private void createAndStoreEvent(String title, Date theDate, String wktPoint) {
        Geometry geom = wktToGeometry(wktPoint);

        if (!geom.getGeometryType().equals("Point")) {
            throw new RuntimeException("Geometry must be a point. Got a " + geom.getGeometryType());
        }

        EntityManager em = JPAUtil.createEntityManager();
        logger.info("createAndStoreEvent() call");
        logger.info("title: "+title+", date: "+theDate+", Geometry: "+geom);

        em.getTransaction().begin();

        Event theEvent = new Event();
        theEvent.setTitle(title);
        theEvent.setDate(theDate);
        theEvent.setLocation((Point) geom);
        em.persist(theEvent);
        em.getTransaction().commit();
        em.close();
    }

    private List find(String wktFilter) {
        Geometry filter = wktToGeometry(wktFilter);
        EntityManager em = JPAUtil.createEntityManager();
        em.getTransaction().begin();
        Query query = em.createQuery("select e from Event e where within(e.location, :filter) = true", Event.class);
        query.setParameter("filter", filter);
        return query.getResultList();
    }

    private Geometry wktToGeometry(String wktPoint) {
        WKTReader fromText = new WKTReader();
        Geometry geom = null;
        try {
            geom = fromText.read(wktPoint);
        } catch (ParseException e) {
            throw new RuntimeException("Not a WKT string:" + wktPoint);
        }
        return geom;
    }

    /**
     * Utility method to assemble all arguments save the first into a String
     */
    private static String assemble(String[] args) {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }
        logger.info(builder.toString());
        return builder.toString();
    }

}