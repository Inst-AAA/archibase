import psycopg2
import geopandas as gpd
import matplotlib.pyplot as plt

con = psycopg2.connect(database="database", user="user", password="password", host="ip", port="port")

sql = "select geom, urban_spaces.name from urban_spaces, features where feature_id = features.id and features.name = 'school';"

school = gpd.GeoDataFrame.from_postgis(sql, con, geom_col='geom' )

school.plot()
plt.show()
