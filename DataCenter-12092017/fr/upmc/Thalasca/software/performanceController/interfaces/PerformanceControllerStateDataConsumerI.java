package fr.upmc.Thalasca.software.performanceController.interfaces;

public interface PerformanceControllerStateDataConsumerI {
	public void acceptPerformanceControllerDynamicData(String performanceControllerURI, 
			PerformanceControllerDynamicStateI currentDynamicState) throws Exception ;

}
