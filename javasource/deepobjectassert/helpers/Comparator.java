package deepobjectassert.helpers;

import java.util.Map;

import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IMendixObject;

import deepobjectassert.repositories.MendixObjectRepository;

public class Comparator {

	private IMendixObject expected;
	private IMendixObject actual;
	private ILogNode logger;
	private MendixObjectRepository mendixObjectRepository = null;

	public Comparator(IMendixObject expected, IMendixObject actual, ILogNode logger,
			MendixObjectRepository mendixObjectRepository) {
		this.expected = expected;
		this.actual = actual;
		this.logger = logger;
		this.mendixObjectRepository = mendixObjectRepository;
	}

	public boolean CompareLists() {

		if (expected == null) {
			logger.error("Expected object is NULL.");
			return false;
		}

		if (actual == null) {
			logger.error("Actual object is NULL.");
			return false;
		}

		if (objectsAreDifferentTypes()) {
			logger.error("Objects are not of the same type. Actual object is of type " + actual.getType()
					+ ". Expected object is of type " + expected.getType() + ".");
			return false;
		}

		Map<String, Object> actualFlattenMendixObject = (new FlattenMendixObject(actual, mendixObjectRepository))
				.getFlattenMendixObject();
		Map<String, Object> expectedFlattenMendixObject = (new FlattenMendixObject(expected, mendixObjectRepository))
				.getFlattenMendixObject();

		return (new CheckMapDifference(logger, expectedFlattenMendixObject, actualFlattenMendixObject))
				.checkDifference();
	}

	private boolean objectsAreDifferentTypes() {
		return (!this.actual.getType().equals(this.expected.getType()));
	}
}