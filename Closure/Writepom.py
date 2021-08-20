import os
import time
import os.path
import subprocess as subp
import sys
import thread




ID=sys.argv[1]
pomfile=open(ID+'/pom.xml')
pomines=pomfile.readlines()
pomfile.close()

result=open(ID+'/newpom.xml','w')

for j in range(len(pomines)):
	if '<artifactId>rhino</artifactId>' in pomines[j].strip():
		result.write(pomines[j])
		result.write('<version>1.'+ID+'</version>')
	else:
		if '<artifactId>rhino</artifactId>' in pomines[j-1].strip():
			continue
		else:
			result.write(pomines[j])	
result.close()
