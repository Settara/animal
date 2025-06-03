package ru.social.animal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.social.animal.model.City;
import ru.social.animal.model.Region;
import ru.social.animal.model.TypeOfAnimal;
import ru.social.animal.repository.CityRepo;
import ru.social.animal.repository.RegionRepo;
import ru.social.animal.repository.TypeOfAnimalRepo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class AnimalApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(AnimalApplication.class, args);
	}

	@Autowired
	private RegionRepo regionRepo;

	@Autowired
	private CityRepo cityRepo;

	@Autowired
	private TypeOfAnimalRepo typeOfAnimalRepo;

	@Override
	public void run(String... args) throws Exception {
		if (regionRepo.count() == 0 && cityRepo.count() == 0) {
			initRegionsAndCities();
		}

		if (typeOfAnimalRepo.count() == 0) {
			initAnimalTypes();
		}
	}

	private void initRegionsAndCities() {
		// 10 регионов
		Region[] regions = {
				new Region(null, "Московская область", new ArrayList<>(), new ArrayList<>()),
				new Region(null, "Ленинградская область", new ArrayList<>(), new ArrayList<>()),
				new Region(null, "Краснодарский край", new ArrayList<>(), new ArrayList<>()),
				new Region(null, "Новосибирская область", new ArrayList<>(), new ArrayList<>()),
				new Region(null, "Свердловская область", new ArrayList<>(), new ArrayList<>()),
				new Region(null, "Республика Татарстан", new ArrayList<>(), new ArrayList<>()),
				new Region(null, "Нижегородская область", new ArrayList<>(), new ArrayList<>()),
				new Region(null, "Ростовская область", new ArrayList<>(), new ArrayList<>()),
				new Region(null, "Челябинская область", new ArrayList<>(), new ArrayList<>()),
				new Region(null, "Республика Башкортостан", new ArrayList<>(), new ArrayList<>())
		};

		regionRepo.saveAll(Arrays.asList(regions));

		// Города для каждого региона (по 3 города)
		List<City> cities = new ArrayList<>();
		cities.addAll(createCitiesForRegion("Москва", "Химки", "Подольск", regions[0]));
		cities.addAll(createCitiesForRegion("Санкт-Петербург", "Гатчина", "Выборг", regions[1]));
		cities.addAll(createCitiesForRegion("Краснодар", "Сочи", "Новороссийск", regions[2]));
		cities.addAll(createCitiesForRegion("Новосибирск", "Бердск", "Искитим", regions[3]));
		cities.addAll(createCitiesForRegion("Екатеринбург", "Нижний Тагил", "Каменск-Уральский", regions[4]));
		cities.addAll(createCitiesForRegion("Казань", "Набережные Челны", "Альметьевск", regions[5]));
		cities.addAll(createCitiesForRegion("Нижний Новгород", "Дзержинск", "Арзамас", regions[6]));
		cities.addAll(createCitiesForRegion("Ростов-на-Дону", "Таганрог", "Шахты", regions[7]));
		cities.addAll(createCitiesForRegion("Челябинск", "Магнитогорск", "Златоуст", regions[8]));
		cities.addAll(createCitiesForRegion("Уфа", "Стерлитамак", "Салават", regions[9]));

		cityRepo.saveAll(cities);
	}

	private List<City> createCitiesForRegion(String city1, String city2, String city3, Region region) {
		return Arrays.asList(
				new City(null, city1, region, new ArrayList<>()),
				new City(null, city2, region, new ArrayList<>()),
				new City(null, city3, region, new ArrayList<>())
		);
	}

	private void initAnimalTypes() {
		List<TypeOfAnimal> animals = Arrays.asList(
				new TypeOfAnimal(null, "Собака", new ArrayList<>()),
				new TypeOfAnimal(null, "Кошка", new ArrayList<>()),
				new TypeOfAnimal(null, "Попугай", new ArrayList<>()),
				new TypeOfAnimal(null, "Кролик", new ArrayList<>()),
				new TypeOfAnimal(null, "Хомяк", new ArrayList<>()),
				new TypeOfAnimal(null, "Черепаха", new ArrayList<>()),
				new TypeOfAnimal(null, "Крыса", new ArrayList<>()),
				new TypeOfAnimal(null, "Фретка", new ArrayList<>()),
				new TypeOfAnimal(null, "Игуана", new ArrayList<>()),
				new TypeOfAnimal(null, "Козёл", new ArrayList<>())
		);

		typeOfAnimalRepo.saveAll(animals);
	}
}
