try:
    import xml.etree.cElementTree as ET
except ImportError:
    import xml.etree.ElementTree as ET

tree = ET.ElementTree(file='Phase-2-Init.xml')
root = tree.getroot()

for idx,submodel in enumerate(root):
	submodel.set('SubmodelId',str(idx))
	submodel.set('description','')


for elem in tree.iterfind('SubModel/Nodes/LevelNodes/LevelNode'):
	elem.set('learnerChangeable','false')

for elem in tree.iterfind('SubModel/Nodes/SourceSinkNodes/SourceSinkNode'):
	ssnid = elem.get('id')
	elem.set('id','S'+ssnid)

for elem in tree.iterfind('SubModel/Nodes/RateNodes/RateNode'):
	elem.set('learnerChangeable','false')

for elem in tree.iterfind('SubModel/Nodes/'):
	for e in elem:
		e.set('shared','false')
		


for elem in tree.iterfind('SubModel/Flows/'):
	if 'SourceSinkNode' in elem.tag:
		for k,v in elem.attrib.iteritems():
			if 'SN' in v:
				elem.set(k,'S'+v)


nodes = root.find('SubModel/Nodes')
constNodes = nodes.find('ConstantNodes')
nodes.remove(constNodes)
nodes.insert(2,constNodes)

auxNodes = nodes.find('AuxiliaryNodes')
nodes.remove(auxNodes)
nodes.insert(3,auxNodes)



tree.write('transform.xml')