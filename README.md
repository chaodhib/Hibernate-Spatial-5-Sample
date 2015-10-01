# Hibernate-Spatial-5-Sample

The code is taken from this tutorial http://www.hibernatespatial.org/documentation/02-Tutorial/01-tutorial4/ which is a tutorial for Hibernate Spatial 4 but I adapted the code and configuration for Hibernate Spatial 5 and a MySQL database.

Use as program arguments:
* "store POINT(10 5)"
in order to save an event named My title with the date and time set to right now and the position set to the point (10 5).
* "find POLYGON((1 1,20 1,20 20,1 20,1 1))"
in order to query every event that happended inside the rectangular defined by the 4 points (1 1), (1 20) (20 1) and (20 20).

note: MySQL56SpatialDialect and MySQL56InnoDBSpatialDialect doesnt seem to work, at least for me. It wrongly make the columns that will store polygons as  tiny blob type. However MySQL5InnoDBSpatialDialect
and MySQLSpatialDialect does. MySQL5InnoDBSpatialDialect is currently used.
