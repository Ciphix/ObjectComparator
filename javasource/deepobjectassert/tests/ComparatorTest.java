package deepobjectassert.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.mendix.core.objectmanagement.member.MendixAutoNumber;
import com.mendix.core.objectmanagement.member.MendixHashString;
import com.mendix.core.objectmanagement.member.MendixObjectReference;
import com.mendix.core.objectmanagement.member.MendixObjectReferenceSet;
import com.mendix.core.objectmanagement.member.MendixString;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IMendixIdentifier;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.mendix.systemwideinterfaces.core.IMendixObjectMember;

import deepobjectassert.helpers.Comparator;
import deepobjectassert.repositories.MendixObjectRepository;

public class ComparatorTest {

	private IMendixObject expectedMock;
	private IMendixObject actualMock;
	private ILogNode loggerMock;
    private Map<String, IMendixObjectMember<?>> expectedMockMap = new HashMap<String, IMendixObjectMember<?>>();
    private Map<String, IMendixObjectMember<?>> actualMockMap = new HashMap<String, IMendixObjectMember<?>>();
    private MendixObjectRepository mendixObjectRepository;

	@Before
	public void setUp() throws Exception {
	    this.expectedMock = mock(IMendixObject.class);
	    this.actualMock = mock(IMendixObject.class);
	    this.loggerMock = mock(ILogNode.class);
	    this.mendixObjectRepository = mock(MendixObjectRepository.class);
	}

	@Test
    public void testIfTrueIsReturnedWhenObjectsAreEqual() {
        when(expectedMock.getType()).thenReturn("Module.Entity");
        when(actualMock.getType()).thenReturn("Module.Entity");
		
		IMendixObjectMember<String> expectedString = mock(MendixString.class);
        when(mendixObjectRepository.getValue(expectedString)).thenReturn("Some string");
        
        expectedMockMap.put("SomeKeyString", expectedString);
        when(mendixObjectRepository.getMembers(expectedMock)).thenReturn(expectedMockMap);

        actualMockMap.put("SomeKeyString", expectedString);
        when(mendixObjectRepository.getMembers(actualMock)).thenReturn(actualMockMap);

        Comparator comparator = new Comparator(expectedMock, actualMock, loggerMock, mendixObjectRepository, true);
        assertTrue(comparator.CompareLists());
        verify(mendixObjectRepository, times(1)).getMembers(expectedMock);
        verify(mendixObjectRepository, times(1)).getMembers(actualMock);
        verify(loggerMock, times(0)).error(Mockito.anyString());
    }

    @Test
    public void testIfTrueIsReturnedWhenValuesAreNull() {
    	when(expectedMock.getType()).thenReturn("Module.Entity");
        when(actualMock.getType()).thenReturn("Module.Entity");
    	
        IMendixObjectMember<String> expectedString = mock(MendixString.class);
        when(mendixObjectRepository.getValue(expectedString)).thenReturn(null);
        
        expectedMockMap.put("SomeKeyString", expectedString);
        when(mendixObjectRepository.getMembers(expectedMock)).thenReturn(expectedMockMap);

        actualMockMap.put("SomeKeyString", expectedString);
        when(mendixObjectRepository.getMembers(actualMock)).thenReturn(actualMockMap);

        Comparator comparator = new Comparator(expectedMock, actualMock, loggerMock, mendixObjectRepository, true);
        assertTrue(comparator.CompareLists());
        verify(mendixObjectRepository, times(1)).getMembers(expectedMock);
        verify(mendixObjectRepository, times(1)).getMembers(actualMock);
        verify(loggerMock, times(0)).error(Mockito.anyString());
    }

    @Test
    public void testIfFalseIsReturnedWhenValuesAreNotEqual() {
    	when(expectedMock.getType()).thenReturn("Module.Entity");
        when(actualMock.getType()).thenReturn("Module.Entity");
        
        // Given we have an expected object with a String value and an actual with another String value
        IMendixObjectMember<String> expectedString = mock(MendixString.class);
        expectedMockMap.put("OtherStringTest", expectedString);
        when(mendixObjectRepository.getValue(expectedString)).thenReturn("Some string");
        
        IMendixObjectMember<String> actualString = mock(MendixString.class);
        when(mendixObjectRepository.getValue(actualString)).thenReturn("Some other string");
        actualMockMap.put("OtherStringTest", actualString);
        
        // And we have an expected object with a null value where actual is a String
        IMendixObjectMember<String> expectedNull = mock(MendixString.class);
        expectedMockMap.put("NullStringTestExpected", expectedNull);
        when(mendixObjectRepository.getValue(expectedNull)).thenReturn(null);
        
        IMendixObjectMember<String> actualSecondString = mock(MendixString.class);
        actualMockMap.put("NullStringTestExpected", actualSecondString);
        when(mendixObjectRepository.getValue(actualSecondString)).thenReturn("Some second string");

        // And we have an expected object with a String where actual is null
        IMendixObjectMember<String> expectedThirdString = mock(MendixString.class);
        expectedMockMap.put("AnotherNullStringTestActual", expectedThirdString);
        when(mendixObjectRepository.getValue(expectedThirdString)).thenReturn("Some third string");
        
        IMendixObjectMember<String> actualNull = mock(MendixString.class);
        actualMockMap.put("AnotherNullStringTestActual", actualNull);
        when(mendixObjectRepository.getValue(actualNull)).thenReturn(null);
           
        when(mendixObjectRepository.getMembers(expectedMock)).thenReturn(expectedMockMap);
        when(mendixObjectRepository.getMembers(actualMock)).thenReturn(actualMockMap);

        // When the comparator is called
        Comparator comparator = new Comparator(expectedMock, actualMock, loggerMock, mendixObjectRepository, true);
        
        // Then we expect False to be returned
        assertFalse(comparator.CompareLists());
        
        // And that the mocks are called
        verify(mendixObjectRepository, times(1)).getMembers(expectedMock);
        verify(mendixObjectRepository, times(1)).getMembers(actualMock);
        verify(loggerMock, times(3)).debug(Mockito.anyString());
    }
    
    @Test
    public void testIfDefaultKeysAreProperlyDeleted() {
    	when(expectedMock.getType()).thenReturn("Module.Entity");
        when(actualMock.getType()).thenReturn("Module.Entity");
        
        // Given we have an expected object with a all the default keys + another string
        IMendixObjectMember<String> expectedString = mock(MendixString.class);
        IMendixObjectMember<String> expectedHashString = mock(MendixHashString.class);
        IMendixObjectMember<Long> expectedAutoNumber = mock(MendixAutoNumber.class);
        expectedMockMap.put("changedDate", expectedString);
        expectedMockMap.put("createdDate", expectedString);
        expectedMockMap.put("System.changedBy", expectedString);
        expectedMockMap.put("System.owner", expectedString);
        expectedMockMap.put("HashString", expectedHashString);
        expectedMockMap.put("AutoNumber", expectedAutoNumber);
        expectedMockMap.put("OtherStringTest", expectedString);
        when(mendixObjectRepository.getValue(expectedString)).thenReturn("Some string");
        when(mendixObjectRepository.getValue(expectedHashString)).thenReturn("Some hash");
        when(mendixObjectRepository.getValue(expectedAutoNumber)).thenReturn(12345L);
            
        // And we have an actual object without the default keys
        actualMockMap.put("OtherStringTest", expectedString);
        
        when(mendixObjectRepository.getMembers(expectedMock)).thenReturn(expectedMockMap);
        when(mendixObjectRepository.getMembers(actualMock)).thenReturn(actualMockMap);

        // When the comparator is called
        Comparator comparator = new Comparator(expectedMock, actualMock, loggerMock, mendixObjectRepository, true);
        
        // Then we expect True to be returned
        assertTrue(comparator.CompareLists());
        
        // And that the mocks are called
        verify(mendixObjectRepository, times(1)).getMembers(expectedMock);
        verify(mendixObjectRepository, times(1)).getMembers(actualMock);
        verify(loggerMock, times(0)).error(Mockito.anyString());
    }
    
    @Test
    public void testIfFalseIsReturnedWhenIMendixObjectIsNull() {
      // When: Expected and Actual objects are null
    	Comparator comparator = new Comparator(null, actualMock, loggerMock, mendixObjectRepository, true);
    	Comparator anotherComparator = new Comparator(expectedMock, null, loggerMock, mendixObjectRepository, true);
      
      // Then: I expect false to be returned
      assertFalse(comparator.CompareLists());
      assertFalse(anotherComparator.CompareLists());
      
      // And: I expect a log message
      verify(loggerMock, times(2)).debug(Mockito.anyString());
    }
    
    @Test
    public void testIfTrueIsReturnedWhenObjectsAreEqualIncludingReference() {
    	when(expectedMock.getType()).thenReturn("Module.Entity");
        when(actualMock.getType()).thenReturn("Module.Entity");
        
    	IMendixObjectMember<String> expectedString = mock(MendixString.class);
        when(mendixObjectRepository.getValue(expectedString)).thenReturn("Some string");
        
        IMendixObjectMember<?> expectedReference = mock(MendixObjectReference.class);
        IMendixIdentifier expectedIdentifier = mock(IMendixIdentifier.class);
        
        expectedMockMap.put("SomeObject_OtherObject", expectedReference);
        actualMockMap.put("SomeObject_OtherObject", expectedReference);
        
        
        expectedMockMap.put("SomeKeyString", expectedString);
        actualMockMap.put("SomeKeyString", expectedString);
        
        IMendixObject expectedAssociatedMock = mock(IMendixObject.class);
        Map<String, IMendixObjectMember<?>> expectedAssociatedMockMap = new HashMap<String, IMendixObjectMember<?>>();
        expectedAssociatedMockMap.put("SomeAssociatedString", expectedString);
        List<IMendixObject> expectedAssociatedObjectList = new ArrayList<>();
        expectedAssociatedObjectList.add(expectedAssociatedMock);
        
        when(mendixObjectRepository.getValue(expectedReference)).thenReturn(expectedIdentifier);
        when(expectedReference.getName()).thenReturn("SomeObject_OtherObject");
        when(mendixObjectRepository.getMembers(expectedAssociatedMock)).thenReturn(expectedAssociatedMockMap);
        when(mendixObjectRepository.getMembers(expectedMock)).thenReturn(expectedMockMap);
        when(mendixObjectRepository.getMembers(actualMock)).thenReturn(actualMockMap);
        when(mendixObjectRepository.retrieveAssociatedObjects(expectedMock, "SomeObject_OtherObject")).thenReturn(expectedAssociatedObjectList);
        when(mendixObjectRepository.retrieveAssociatedObjects(actualMock, "SomeObject_OtherObject")).thenReturn(expectedAssociatedObjectList);
        

        Comparator comparator = new Comparator(expectedMock, actualMock, loggerMock, mendixObjectRepository, true);
        assertTrue(comparator.CompareLists());
        verify(mendixObjectRepository, times(1)).getMembers(expectedMock);
        verify(mendixObjectRepository, times(1)).getMembers(actualMock);
        verify(mendixObjectRepository, times(2)).getMembers(expectedAssociatedMock);
        verify(loggerMock, times(0)).error(Mockito.anyString());
    }
    
    @Test
    public void testIfSimilarReferencesAreEvaluatedOnce() {
    	when(expectedMock.getType()).thenReturn("Module.Entity");
        when(actualMock.getType()).thenReturn("Module.Entity");
        
    	IMendixObjectMember<String> expectedString = mock(MendixString.class);
        when(mendixObjectRepository.getValue(expectedString)).thenReturn("Some string");
        
        IMendixObjectMember<?> expectedReference = mock(MendixObjectReference.class);
        IMendixIdentifier expectedIdentifier = mock(IMendixIdentifier.class);
        when(mendixObjectRepository.getValue(expectedReference)).thenReturn(expectedIdentifier);
        when(expectedReference.getName()).thenReturn("SomeObject_OtherObject");
        expectedMockMap.put("SomeObject_OtherObject", expectedReference);
        actualMockMap.put("SomeObject_OtherObject", expectedReference);
        
        IMendixObject expectedAssociatedMock = mock(IMendixObject.class);
        Map<String, IMendixObjectMember<?>> expectedAssociatedMockMap = new HashMap<String, IMendixObjectMember<?>>();
        expectedAssociatedMockMap.put("SomeAssociatedString", expectedString);
        expectedAssociatedMockMap.put("SomeObject_OtherObject", expectedReference);
        List<IMendixObject> expectedAssociatedObjectList = new ArrayList<>();
        expectedAssociatedObjectList.add(expectedAssociatedMock);
        
        when(mendixObjectRepository.getMembers(expectedAssociatedMock)).thenReturn(expectedAssociatedMockMap);
        when(mendixObjectRepository.getMembers(expectedMock)).thenReturn(expectedMockMap);
        when(mendixObjectRepository.getMembers(actualMock)).thenReturn(actualMockMap);
        when(mendixObjectRepository.retrieveAssociatedObjects(expectedMock, "SomeObject_OtherObject")).thenReturn(expectedAssociatedObjectList);
        when(mendixObjectRepository.retrieveAssociatedObjects(actualMock, "SomeObject_OtherObject")).thenReturn(expectedAssociatedObjectList);
        when(mendixObjectRepository.retrieveAssociatedObjects(expectedAssociatedMock, "SomeObject_OtherObject")).thenReturn(expectedAssociatedObjectList);
        

        Comparator comparator = new Comparator(expectedMock, actualMock, loggerMock, mendixObjectRepository, true);
        assertTrue(comparator.CompareLists());
        
        verify(mendixObjectRepository, times(0)).retrieveAssociatedObjects(expectedAssociatedMock, "SomeObject_OtherObject");
        verify(loggerMock, times(0)).error(Mockito.anyString());
    }
    
    @Test
    public void testIfTrueIsReturnedWhenObjectsAreEqualIncludingReferenceSets() {
    	when(expectedMock.getType()).thenReturn("Module.Entity");
        when(actualMock.getType()).thenReturn("Module.Entity");
        
    	IMendixObjectMember<String> expectedString = mock(MendixString.class);
        when(mendixObjectRepository.getValue(expectedString)).thenReturn("Some string");
        
        IMendixObjectMember<?> expectedReferenceSet = mock(MendixObjectReferenceSet.class);
        IMendixIdentifier expectedIdentifier = mock(IMendixIdentifier.class);
        List<IMendixIdentifier> expectedIdentifierList = new ArrayList<>();
        expectedIdentifierList.add(expectedIdentifier);
        when(mendixObjectRepository.getValue(expectedReferenceSet)).thenReturn(expectedIdentifierList);
        when(expectedReferenceSet.getName()).thenReturn("SomeObject_OtherObject");
        expectedMockMap.put("SomeObject_OtherObject", expectedReferenceSet);
        actualMockMap.put("SomeObject_OtherObject", expectedReferenceSet);
        
        
        expectedMockMap.put("SomeKeyString", expectedString);
        actualMockMap.put("SomeKeyString", expectedString);
        
        IMendixObject expectedAssociatedMock = mock(IMendixObject.class);
        Map<String, IMendixObjectMember<?>> expectedAssociatedMockMap = new HashMap<String, IMendixObjectMember<?>>();
        expectedAssociatedMockMap.put("SomeAssociatedString", expectedString);
        List<IMendixObject> expectedAssociatedObjectList = new ArrayList<>();
        expectedAssociatedObjectList.add(expectedAssociatedMock);
        
        when(mendixObjectRepository.getMembers(expectedAssociatedMock)).thenReturn(expectedAssociatedMockMap);
        when(mendixObjectRepository.getMembers(expectedMock)).thenReturn(expectedMockMap);
        when(mendixObjectRepository.getMembers(actualMock)).thenReturn(actualMockMap);
        when(mendixObjectRepository.retrieveAssociatedObjects(expectedMock, "SomeObject_OtherObject")).thenReturn(expectedAssociatedObjectList);
        when(mendixObjectRepository.retrieveAssociatedObjects(actualMock, "SomeObject_OtherObject")).thenReturn(expectedAssociatedObjectList);
        

        Comparator comparator = new Comparator(expectedMock, actualMock, loggerMock, mendixObjectRepository, true);
        assertTrue(comparator.CompareLists());
        verify(mendixObjectRepository, times(1)).getMembers(expectedMock);
        verify(mendixObjectRepository, times(1)).getMembers(actualMock);
        verify(mendixObjectRepository, times(2)).getMembers(expectedAssociatedMock);
        verify(loggerMock, times(0)).error(Mockito.anyString());
    }
    
    @Test
    public void testIfSimilarReferencesSetsAreEvaluatedOnce() {
    	when(expectedMock.getType()).thenReturn("Module.Entity");
        when(actualMock.getType()).thenReturn("Module.Entity");
        
    	IMendixObjectMember<String> expectedString = mock(MendixString.class);
        when(mendixObjectRepository.getValue(expectedString)).thenReturn("Some string");
        
        IMendixObjectMember<?> expectedReferenceSet = mock(MendixObjectReferenceSet.class);
        IMendixIdentifier expectedIdentifier = mock(IMendixIdentifier.class);
        List<IMendixIdentifier> expectedIdentifierList = new ArrayList<>();
        expectedIdentifierList.add(expectedIdentifier);
        when(mendixObjectRepository.getValue(expectedReferenceSet)).thenReturn(expectedIdentifierList);
        when(expectedReferenceSet.getName()).thenReturn("SomeObject_OtherObject");
        expectedMockMap.put("SomeObject_OtherObject", expectedReferenceSet);
        actualMockMap.put("SomeObject_OtherObject", expectedReferenceSet);
        
        IMendixObject expectedAssociatedMock = mock(IMendixObject.class);
        Map<String, IMendixObjectMember<?>> expectedAssociatedMockMap = new HashMap<String, IMendixObjectMember<?>>();
        expectedAssociatedMockMap.put("SomeAssociatedString", expectedString);
        expectedAssociatedMockMap.put("SomeObject_OtherObject", expectedReferenceSet);
        List<IMendixObject> expectedAssociatedObjectList = new ArrayList<>();
        expectedAssociatedObjectList.add(expectedAssociatedMock);
        
        when(mendixObjectRepository.getMembers(expectedAssociatedMock)).thenReturn(expectedAssociatedMockMap);
        when(mendixObjectRepository.getMembers(expectedMock)).thenReturn(expectedMockMap);
        when(mendixObjectRepository.getMembers(actualMock)).thenReturn(actualMockMap);
        when(mendixObjectRepository.retrieveAssociatedObjects(expectedMock, "SomeObject_OtherObject")).thenReturn(expectedAssociatedObjectList);
        when(mendixObjectRepository.retrieveAssociatedObjects(actualMock, "SomeObject_OtherObject")).thenReturn(expectedAssociatedObjectList);
        when(mendixObjectRepository.retrieveAssociatedObjects(expectedAssociatedMock, "SomeObject_OtherObject")).thenReturn(expectedAssociatedObjectList);
        

        Comparator comparator = new Comparator(expectedMock, actualMock, loggerMock, mendixObjectRepository, true);
        assertTrue(comparator.CompareLists());
        
        verify(mendixObjectRepository, times(0)).retrieveAssociatedObjects(expectedAssociatedMock, "SomeObject_OtherObject");
        verify(loggerMock, times(0)).error(Mockito.anyString());
        }
    
    @Test
    public void testIfFalseIsReturnedWhenAssociatedOjectsAreUnequal() {
    	when(expectedMock.getType()).thenReturn("Module.Entity");
        when(actualMock.getType()).thenReturn("Module.Entity");
        
    	IMendixObjectMember<String> expectedString = mock(MendixString.class);
        when(mendixObjectRepository.getValue(expectedString)).thenReturn("Some string");
        
        IMendixObjectMember<String> actualString = mock(MendixString.class);
        when(mendixObjectRepository.getValue(actualString)).thenReturn("Some different string");
        
        IMendixObjectMember<?> expectedReference = mock(MendixObjectReference.class);
        IMendixIdentifier expectedIdentifier = mock(IMendixIdentifier.class);
        
        IMendixObjectMember<?> actualReference = mock(MendixObjectReference.class);
        IMendixIdentifier actualIdentifier = mock(IMendixIdentifier.class);

        
        expectedMockMap.put("SomeObject_OtherObject", expectedReference);
        actualMockMap.put("SomeObject_OtherObject", actualReference);
        
        expectedMockMap.put("SomeKeyString", expectedString);
        actualMockMap.put("SomeKeyString", expectedString);
        
        IMendixObject expectedAssociatedMock = mock(IMendixObject.class);
        Map<String, IMendixObjectMember<?>> expectedAssociatedMockMap = new HashMap<String, IMendixObjectMember<?>>();
        expectedAssociatedMockMap.put("SomeAssociatedString", expectedString);
        List<IMendixObject> expectedAssociatedObjectList = new ArrayList<>();
        expectedAssociatedObjectList.add(expectedAssociatedMock);
        
        IMendixObject actualAssociatedMock = mock(IMendixObject.class);
        Map<String, IMendixObjectMember<?>> actualAssociatedMockMap = new HashMap<String, IMendixObjectMember<?>>();
        actualAssociatedMockMap.put("SomeAssociatedString", actualString);
        List<IMendixObject> actualAssociatedObjectList = new ArrayList<>();
        actualAssociatedObjectList.add(actualAssociatedMock);
        
        when(mendixObjectRepository.getValue(expectedReference)).thenReturn(expectedIdentifier);
        when(expectedReference.getName()).thenReturn("SomeObject_OtherObject");
        when(mendixObjectRepository.getMembers(expectedAssociatedMock)).thenReturn(expectedAssociatedMockMap);
        
        when(mendixObjectRepository.getValue(actualReference)).thenReturn(actualIdentifier);
        when(actualReference.getName()).thenReturn("SomeObject_OtherObject");
        when(mendixObjectRepository.getMembers(actualAssociatedMock)).thenReturn(actualAssociatedMockMap);
        
        when(mendixObjectRepository.getMembers(expectedMock)).thenReturn(expectedMockMap);
        when(mendixObjectRepository.getMembers(actualMock)).thenReturn(actualMockMap);
        when(mendixObjectRepository.retrieveAssociatedObjects(expectedMock, "SomeObject_OtherObject")).thenReturn(expectedAssociatedObjectList);
        when(mendixObjectRepository.retrieveAssociatedObjects(actualMock, "SomeObject_OtherObject")).thenReturn(actualAssociatedObjectList);
        

        Comparator comparator = new Comparator(expectedMock, actualMock, loggerMock, mendixObjectRepository, true);
        assertFalse(comparator.CompareLists());
        verify(mendixObjectRepository, times(1)).getMembers(expectedMock);
        verify(mendixObjectRepository, times(1)).getMembers(actualMock);
        verify(mendixObjectRepository, times(1)).getMembers(expectedAssociatedMock);
        verify(mendixObjectRepository, times(1)).getMembers(actualAssociatedMock);
        verify(loggerMock, times(1)).debug(Mockito.anyString());
    }
    
    @Test
    public void testIfFalseIfObjectsAreOfDifferentTypes() {
    	when(expectedMock.getType()).thenReturn("Module.Entity");
        when(actualMock.getType()).thenReturn("Module.AnotherEntity");
        
    	Comparator comparator = new Comparator(expectedMock, actualMock, loggerMock, mendixObjectRepository, true);

    	assertFalse(comparator.CompareLists());
    	verify(loggerMock, times(1)).debug(Mockito.anyString());
    }
    
    @Test
    public void testIfFalseIsReturnedWhenAssociatedObjectsIsNotSet() {
    	when(expectedMock.getType()).thenReturn("Module.Entity");
        when(actualMock.getType()).thenReturn("Module.Entity");
        
    	IMendixObjectMember<String> expectedString = mock(MendixString.class);
        when(mendixObjectRepository.getValue(expectedString)).thenReturn("Some string");
        
        IMendixObjectMember<String> actualString = mock(MendixString.class);
        when(mendixObjectRepository.getValue(actualString)).thenReturn("Some different string");
        
        IMendixObjectMember<?> expectedReference = mock(MendixObjectReference.class);
        IMendixIdentifier expectedIdentifier = mock(IMendixIdentifier.class);
        
        expectedMockMap.put("SomeObject_OtherObject", expectedReference);
        
        expectedMockMap.put("SomeKeyString", expectedString);
        actualMockMap.put("SomeKeyString", expectedString);
        
        IMendixObject expectedAssociatedMock = mock(IMendixObject.class);
        Map<String, IMendixObjectMember<?>> expectedAssociatedMockMap = new HashMap<String, IMendixObjectMember<?>>();
        expectedAssociatedMockMap.put("SomeAssociatedString", expectedString);
        List<IMendixObject> expectedAssociatedObjectList = new ArrayList<>();
        expectedAssociatedObjectList.add(expectedAssociatedMock);
        
        when(mendixObjectRepository.getValue(expectedReference)).thenReturn(expectedIdentifier);
        when(expectedReference.getName()).thenReturn("SomeObject_OtherObject");
        when(mendixObjectRepository.getMembers(expectedAssociatedMock)).thenReturn(expectedAssociatedMockMap);
        
        when(mendixObjectRepository.getMembers(expectedMock)).thenReturn(expectedMockMap);
        when(mendixObjectRepository.getMembers(actualMock)).thenReturn(actualMockMap);
        when(mendixObjectRepository.retrieveAssociatedObjects(expectedMock, "SomeObject_OtherObject")).thenReturn(expectedAssociatedObjectList);
        

        Comparator comparator = new Comparator(expectedMock, actualMock, loggerMock, mendixObjectRepository, true);
        Comparator anotherComparator = new Comparator(actualMock, expectedMock, loggerMock, mendixObjectRepository, true);
        assertFalse(comparator.CompareLists());
        assertFalse(anotherComparator.CompareLists());
        
        verify(mendixObjectRepository, times(2)).getMembers(expectedMock);
        verify(mendixObjectRepository, times(2)).getMembers(actualMock);
        verify(mendixObjectRepository, times(2)).getMembers(expectedAssociatedMock);
 
        verify(loggerMock, times(2)).debug(Mockito.anyString());
    }
    
    @Test
    public void testIfTrueIsReturnedWhenAssociatedOjectsAreUnequalAndAssociatedObjectsAreNotEvaluated() {
    	when(expectedMock.getType()).thenReturn("Module.Entity");
        when(actualMock.getType()).thenReturn("Module.Entity");
        
    	IMendixObjectMember<String> expectedString = mock(MendixString.class);
        when(mendixObjectRepository.getValue(expectedString)).thenReturn("Some string");
        
        IMendixObjectMember<String> actualString = mock(MendixString.class);
        when(mendixObjectRepository.getValue(actualString)).thenReturn("Some different string");
        
        IMendixObjectMember<?> expectedReference = mock(MendixObjectReference.class);
        IMendixIdentifier expectedIdentifier = mock(IMendixIdentifier.class);
        
        IMendixObjectMember<?> actualReference = mock(MendixObjectReference.class);
        IMendixIdentifier actualIdentifier = mock(IMendixIdentifier.class);

        
        expectedMockMap.put("SomeObject_OtherObject", expectedReference);
        actualMockMap.put("SomeObject_OtherObject", actualReference);
        
        expectedMockMap.put("SomeKeyString", expectedString);
        actualMockMap.put("SomeKeyString", expectedString);
        
        IMendixObject expectedAssociatedMock = mock(IMendixObject.class);
        Map<String, IMendixObjectMember<?>> expectedAssociatedMockMap = new HashMap<String, IMendixObjectMember<?>>();
        expectedAssociatedMockMap.put("SomeAssociatedString", expectedString);
        List<IMendixObject> expectedAssociatedObjectList = new ArrayList<>();
        expectedAssociatedObjectList.add(expectedAssociatedMock);
        
        IMendixObject actualAssociatedMock = mock(IMendixObject.class);
        Map<String, IMendixObjectMember<?>> actualAssociatedMockMap = new HashMap<String, IMendixObjectMember<?>>();
        actualAssociatedMockMap.put("SomeAssociatedString", actualString);
        List<IMendixObject> actualAssociatedObjectList = new ArrayList<>();
        actualAssociatedObjectList.add(actualAssociatedMock);
        
        when(mendixObjectRepository.getValue(expectedReference)).thenReturn(expectedIdentifier);
        when(expectedReference.getName()).thenReturn("SomeObject_OtherObject");
        when(mendixObjectRepository.getMembers(expectedAssociatedMock)).thenReturn(expectedAssociatedMockMap);
        
        when(mendixObjectRepository.getValue(actualReference)).thenReturn(actualIdentifier);
        when(actualReference.getName()).thenReturn("SomeObject_OtherObject");
        when(mendixObjectRepository.getMembers(actualAssociatedMock)).thenReturn(actualAssociatedMockMap);
        
        when(mendixObjectRepository.getMembers(expectedMock)).thenReturn(expectedMockMap);
        when(mendixObjectRepository.getMembers(actualMock)).thenReturn(actualMockMap);
        when(mendixObjectRepository.retrieveAssociatedObjects(expectedMock, "SomeObject_OtherObject")).thenReturn(expectedAssociatedObjectList);
        when(mendixObjectRepository.retrieveAssociatedObjects(actualMock, "SomeObject_OtherObject")).thenReturn(actualAssociatedObjectList);
        

        Comparator comparator = new Comparator(expectedMock, actualMock, loggerMock, mendixObjectRepository, false);
        assertTrue(comparator.CompareLists());
        verify(mendixObjectRepository, times(1)).getMembers(expectedMock);
        verify(mendixObjectRepository, times(1)).getMembers(actualMock);
        verify(mendixObjectRepository, times(0)).getMembers(expectedAssociatedMock);
        verify(mendixObjectRepository, times(0)).getMembers(actualAssociatedMock);
    }
}
