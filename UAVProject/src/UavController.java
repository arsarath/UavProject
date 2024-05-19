import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class UavController {
	private static final int MAX_UAV_SIZE = 2000;

	private Vector<UavModel> uavModels;
	private Timer uavControllerTimer = new Timer();
	private Queue<Integer> uavIdQueue;

	public UavController() {
		uavModels = new Vector<UavModel>();
		uavIdQueue = new LinkedList<Integer>();

		generateUniqueIds();
		addRandomModels();
		uavControllerTimerTask();
	}

	private void uavControllerTimerTask() {
		TimerTask updateTask = new TimerTask() {
			@Override
			public void run() {
				decreaseBatteryPercentage();
			}
		};

		uavControllerTimer.schedule(updateTask, 0, 60000);

	}

	public Vector<UavModelDto> createDtosForUiUpdate() {
		var dtos = new Vector<UavModelDto>();

		synchronized (uavModels) {
			for (var uavModel : uavModels) {
				var dto = new UavModelDto();

				dto.uavId = uavModel.getUavId();
				dto.uavCode = uavModel.getUavCode();
				dto.uavBatteryPercentage = uavModel.getUavBatteryPercentage();
				dto.uavGeoPosition = uavModel.getUavGeoPosition();
				dto.uavSpeed = uavModel.getUavSpeed();
				dto.aliveTimeStr = uavModel.getAliveTimeAsStr();

				dtos.add(dto);
			}
		}

		return dtos;
	}

	public void fillDtosSpecificArea(Vector<UavModelDto> dtos, int sortColumnIndex, boolean ascending) {
		var lowerThan25BatteryDtos = new Vector<UavModelDto>();
		var moreThan25BatteryDtos = new Vector<UavModelDto>();

		for (var dto : dtos) {
			if (dto.uavBatteryPercentage < 25) {
				lowerThan25BatteryDtos.add(dto);
				dto.orangeBackground = true;
			} else
				moreThan25BatteryDtos.add(dto);
		}

		lowerThan25BatteryDtos.sort(new Comparator<UavModelDto>() {
			public int compare(UavModelDto model1, UavModelDto model2) {
				return dtoComparator(sortColumnIndex, ascending, model1, model2);
			}
		});

		moreThan25BatteryDtos.sort(new Comparator<UavModelDto>() {
			public int compare(UavModelDto model1, UavModelDto model2) {
				return dtoComparator(sortColumnIndex, ascending, model1, model2);
			}
		});

		var rowIndex = 0;

		for (var dto : lowerThan25BatteryDtos)
			dto.rowIndex = rowIndex++;

		for (var dto : moreThan25BatteryDtos)
			dto.rowIndex = rowIndex++;
	}

	private int dtoComparator(int sortColumnIndex, boolean ascending, UavModelDto model1,
			UavModelDto model2) {
		switch (sortColumnIndex) {
			case 0:
				if (ascending)
					return Integer.compare(model1.uavId, model2.uavId);
				else
					return Integer.compare(model2.uavId, model1.uavId);

			case 1:
				if (ascending)
					return model1.uavCode.compareTo(model2.uavCode);
				else
					return model2.uavCode.compareTo(model1.uavCode);

			case 2:
				if (ascending)
					return Float.compare(model1.uavBatteryPercentage, model2.uavBatteryPercentage);
				else
					return Float.compare(model2.uavBatteryPercentage, model1.uavBatteryPercentage);

			case 3:
				if (ascending)
					return model1.uavGeoPosition.compareTo(model2.uavGeoPosition);
				else
					return model2.uavGeoPosition.compareTo(model1.uavGeoPosition);

			case 4:
				if (ascending)
					return Float.compare(model1.uavSpeed, model2.uavSpeed);
				else
					return Float.compare(model2.uavSpeed, model1.uavSpeed);

			case 5:
				if (ascending)
					return model1.aliveTimeStr.compareTo(model2.aliveTimeStr);
				else
					return model2.aliveTimeStr.compareTo(model1.aliveTimeStr);
			default:
				return 0;
		}
	}

	public void decreaseBatteryPercentage() {
		synchronized (uavModels) {
			for (var model : uavModels) 
			{
				float decreasePercentage = 0.2f / 100;

				float battVal = (float) (model.getUavBatteryPercentage());
				battVal = battVal - decreasePercentage * battVal;
				battVal = Math.round(battVal * 100.0f) / 100.0f;
				model.setUavBatteryPercentage(battVal);
			}
		}

	}

	public boolean uavIdIsUsed(int index) {

		return false;
	}

	public UavModel addUavModel(String name, String battery, String lattitude, String longtitude, String altitude,
			String speed) {
		var uavId = getUavId();

		if (uavId == -1)
			return null;

		var model = new UavModel(uavId);
		
		model.setUavCode(name);
		model.setUavBatteryPercentage(Float.parseFloat(battery));
		model.setUavGeoPosition(lattitude + " / " + longtitude + " / " + altitude);
		model.setUavSpeed(Float.parseFloat(speed));

		synchronized (uavModels) {
			uavModels.add(model);
		}

		return model;
	}

	public void deleteUavModel(int uavId) {
		synchronized (uavModels) {
			for (var i = 0; i < uavModels.size(); i++) {
				if (uavModels.get(i).getUavId() == uavId) {
					uavModels.remove(i);
					break;
				}
			}
		}

		freeUavId(uavId);
	}

	public void updateUavModel(int uavId, String name, String battery, String lattitude, String longtitude,
			String altitude, String speed) {
		synchronized (uavModels) {
			for (var model : uavModels) {
				if (model.getUavId() == uavId) {
					model.setUavCode(name);
					model.setUavBatteryPercentage(Float.parseFloat(battery));
					model.setUavGeoPosition(lattitude + " / " + longtitude + " / " + altitude);
					model.setUavSpeed(Float.parseFloat(speed));

					break;
				}
			}
		}
	}

	private void generateUniqueIds() {
		var uniqueIds = new HashSet<Integer>();

		var random = new Random();

		while (uavIdQueue.size() <= MAX_UAV_SIZE) {
			var uniqueId = random.nextInt(Integer.MAX_VALUE);

			if (!uniqueIds.add(uniqueId))
				continue;

			freeUavId(uniqueId);
		}
	}

	private int getUavId() {
		if (uavIdQueue.size() == 0)
			return -1;

		return uavIdQueue.poll();
	}

	private void freeUavId(int uavId) {
		uavIdQueue.add(uavId);
	}

	public void addRandomModels() {
		
		for (var i = 0; i < 10; i++) {
			if (addUavModel(String.format("AKINCI-%d", i),
					String.format(generateRandomBattery()),
					String.format(generateRandomLatitude()),
					String.format(generateRandomLongtitude()),
					String.format(generateRandomAltitude()),
					String.format(generateRandomSpeed())) == null) {
				break;
			}
		}
		
		for (var i = 0; i < 15; i++) {
			if (addUavModel(String.format("BAHA-%d", i),
					String.format(generateRandomBattery()),
					String.format(generateRandomLatitude()),
					String.format(generateRandomLongtitude()),
					String.format(generateRandomAltitude()),
					String.format(generateRandomSpeed())) == null) {
				break;
			}
		}
	}
	
	private  String generateRandomBattery() {
        Random rand = new Random();
        int randomNumber = rand.nextInt(101);
        return String.valueOf(randomNumber); 
    }
	
	private  String generateRandomLatitude() {
        Random rand = new Random();
        
        double latitude = rand.nextDouble() * 180.0 - 90.0; 
        return String.format("%.2f", latitude);
    }
	
	private  String generateRandomLongtitude() {
        Random rand = new Random();
        
        double latitude = rand.nextDouble() * 360.0 - 180.0; 
        return String.format("%.2f", latitude);
    }
	
	private String generateRandomAltitude() {
        Random rand = new Random();
        int randomAltitude = 1000 + rand.nextInt(9000);
        return String.valueOf(randomAltitude);
    }
	
	private String generateRandomSpeed() {
        Random rand = new Random();
        float randomAltitude = 1 + rand.nextFloat(5);
        return String.format("%.2f", randomAltitude);
    }

}
