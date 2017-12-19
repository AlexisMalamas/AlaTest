package fr.upmc.Thalasca.datacenter.software.javassist;

import java.lang.reflect.Method;
import java.util.HashMap;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class Javassist {


	public static Class<?> makeConnectorClassJavassist(String connectorCanonicalClassName,
			Class<?> connectorSuperclass,
			Class<?> connectorImplementedInterface,
			Class<?> offeredInterface,
			HashMap<String,String> methodNamesMap ) throws Exception
	{
		System.out.println("get pool==============");
		ClassPool pool = ClassPool.getDefault() ;
		System.out.println("get connector superclass");
		CtClass cs = pool.get(connectorSuperclass.getCanonicalName()) ;
		System.out.println("get implemented interface");
		CtClass cii = pool.get(connectorImplementedInterface.getCanonicalName()) ;
		System.out.println("get offered interface");
		CtClass oi = pool.get(offeredInterface.getCanonicalName()) ;
		System.out.println("make class");
		CtClass connectorCtClass = pool.makeClass(connectorCanonicalClassName) ;

		connectorCtClass.setSuperclass(cs) ;
		Method[] methodsToImplement = connectorImplementedInterface.getDeclaredMethods() ;
		for (int i = 0 ; i < methodsToImplement.length ; i++) {
			String source = "public " ;
			source += methodsToImplement[i].getReturnType().getName() + " " ;
			source += methodsToImplement[i].getName() + "(" ; Class<?>[] pt = methodsToImplement[i].getParameterTypes() ;
			String callParam = "" ;
			for (int j = 0 ; j < pt.length ; j++) {
				String pName = "aaa" + j ;
				source += pt[j].getCanonicalName() + " " + pName ; callParam += pName ;
				if (j < pt.length - 1)
				{
					source += ", " ;
					callParam += ", " ;
				}
			}
			source += ")" ;
			Class<?>[] et = methodsToImplement[i].getExceptionTypes() ;
			if (et != null && et.length > 0)
			{
				source += " throws " ;


				for (int z = 0 ; z < et.length ; z++)
				{
					source += et[z].getCanonicalName() ;
					if (z < et.length - 1)
					{
						source += "," ;
					}
				}
			}
			source += "\n{ return ((" ;
			source += offeredInterface.getCanonicalName() + ")this.offering)." ;
			source += methodNamesMap.get(methodsToImplement[i].getName()) ;
			source += "(" + callParam + ") ;\n}" ;
			CtMethod theCtMethod = CtMethod.make(source, connectorCtClass) ;
			connectorCtClass.addMethod(theCtMethod) ;
		}
		connectorCtClass.setInterfaces(new CtClass[]{cii}) ;
		cii.detach() ;
		cs.detach() ;
		if(!connectorImplementedInterface.equals(offeredInterface))
			oi.detach() ;
		Class<?> ret = connectorCtClass.toClass() ;
		connectorCtClass.detach() ;

		return ret ;
	}
}
