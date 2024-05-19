import java.util.concurrent.TimeUnit;

public class UavModel {
	private int uavId;
	private String uavCode;
	private float uavBatteryPercentage;
	private String uavGeoPosition;
	private float uavSpeed;
	private long creationTimeMs;

	public UavModel(int Id) {
		uavId = Id;
		creationTimeMs = System.currentTimeMillis();
	}

	public int getUavId() {
		return uavId;
	}

	// GETTERS

	public String getUavCode() {
		return uavCode;
	}

	public float getUavBatteryPercentage() {
		return uavBatteryPercentage;
	}

	public String getUavGeoPosition() {
		return uavGeoPosition;
	}

	public float getUavSpeed() {
		return uavSpeed;
	}

	// SETTERS
	public void setUavCode(String uavCodeValue) {
		uavCode = uavCodeValue;
	}

	public void setUavBatteryPercentage(float uavBatteryPercentageValue) {
		uavBatteryPercentage = uavBatteryPercentageValue;
	}

	public void setUavGeoPosition(String uavGeoPositionValue) {
		uavGeoPosition = uavGeoPositionValue;
	}

	public void setUavSpeed(float uavSpeedValue) {
		uavSpeed = uavSpeedValue;
	}

	@Override
	public String toString() {

		return "Name: " + getUavCode() + "   Battery: " + getUavBatteryPercentage() + "   GeoPos: "
				+ getUavGeoPosition();
	}

	public long getAliveTimeMs() {
		return System.currentTimeMillis() - creationTimeMs;
	}

	public String getAliveTimeAsStr() {
		var aliveTimeMs = getAliveTimeMs();

		var aliveTimeStr = String.format("%02d : %02d : %02d",
				TimeUnit.MILLISECONDS.toHours(aliveTimeMs) % 24,
				TimeUnit.MILLISECONDS.toMinutes(aliveTimeMs) % 60,
				TimeUnit.MILLISECONDS.toSeconds(aliveTimeMs) % 60);

		return aliveTimeStr;
	}

}
