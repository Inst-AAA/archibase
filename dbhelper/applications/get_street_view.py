import psycopg2
from shapely.wkt import dumps, loads

con = psycopg2.connect(database="database", user="user", password="password", host="ip", port="port")

cur = con.cursor()
cur.execute("select id, st_astext(geom) from roads where roadtype = 'S2';")
rows = cur.fetchall()

pts = []

for row in rows:
    line = loads(row[1])
    pts.append(line.coords[0])

print(pts[3])
print(len(pts))

import random
part_pts = random.sample(pts, 100)

print(len(part_pts))
print(part_pts[0])
# print("Database opened successfully")

# Import google_streetview for the api module
import google_streetview.api

# Define parameters for street view api
params = []

for pt in part_pts:
    params.append({
	'size': '600x300', # max 640x64c pixels
	'location': (str(pt[1]) + ', ' + str(pt[0])),
	'heading': '90',
	'pitch': '-0.76',
	'key': 'GOOGLE API KEY'
    })

for param in params:
    print(param['location'])
# Create a results object

results = google_streetview.api.results(params)

# Download images to directory 'downloads'
results.download_links('../../data/streetview')
