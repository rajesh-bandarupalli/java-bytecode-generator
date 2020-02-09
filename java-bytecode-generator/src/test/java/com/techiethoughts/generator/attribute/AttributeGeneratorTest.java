package com.techiethoughts.generator.attribute;

import com.techiethoughts.AbstractPersistableUnitTests;
import com.techiethoughts.domains.core.AttributeDetail;
import com.techiethoughts.domains.core.ModelDetail;
import com.techiethoughts.util.AppConstants;
import com.techiethoughts.util.ByteCodeGenUtil;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.techiethoughts.domains.core.AttributeDetail.Type.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Rajesh Bandarupalli
 */

public class AttributeGeneratorTest extends AbstractPersistableUnitTests {

	private static AtomicLong version = new AtomicLong(100);

	/*
	 * Test case to verify agent is able to create generic field as an attribute for dynamically generated classes. 
	 * This test case verifies, private Map<String,Double> itemPrice; attribute will be created in ShoppingCart.class.
	 */
	@Test
	public void testGenericAttribute() {


		ModelDetail model = getModel();

		AttributeDetail.GenericInfo genericInfo = new AttributeDetail.GenericInfo();
		genericInfo.setRawType("java.util.Map");
		genericInfo.setParameters(Arrays.asList("java.lang.String", "java.lang.Double"));

		AttributeDetail attrDetail = new AttributeDetail();
		attrDetail.setName("itemPrice");
		attrDetail.setType(GENERIC);
		attrDetail.setGenericInfo(genericInfo);
		model.setAttributes(Arrays.asList(attrDetail));

		publisher.publishModel(model);

		try {
			Class<?> clazz = Class.forName(ByteCodeGenUtil.getFullyQualifiedClassName(model), true,
					this.getClass().getClassLoader());
			assertNotNull(clazz);
			Field field = clazz.getDeclaredField("itemPrice");
			assertNotNull(field);
			Type fieldType = field.getGenericType();
			assertTrue(fieldType instanceof ParameterizedType);
			ParameterizedType genericFieldType = (ParameterizedType) fieldType;
			assertEquals(Map.class, genericFieldType.getRawType());
			assertEquals(String.class, genericFieldType.getActualTypeArguments()[0]);
			assertEquals(Double.class, genericFieldType.getActualTypeArguments()[1]);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (Exception e) {
			assertTrue(false);
		}

	}

	/*
	 * Test case to verify agent is able to create existing class as an attribute to dynamically created class.
	 * This test case verifies, private LoadedClazz loadedClazz; attribute will be created in ShoppingCart.class.
	 */
	@Test
	public void testModelAttribute() {

		ModelDetail model = getModel();

		ModelDetail assessmentFormModel = new ModelDetail();
		assessmentFormModel.setName("AttributeGeneratorTest$LoadedClazz");
		assessmentFormModel.setPackageName("com.techiethoughts.generator.attribute");

		AttributeDetail attrDetail = new AttributeDetail();
		attrDetail.setName("loadedClazz");
		attrDetail.setType(MODEL);
		attrDetail.setModelDetail(assessmentFormModel);

		model.setAttributes(Arrays.asList(attrDetail));

		publisher.publishModel(model);

		try {
			Class<?> clazz = Class.forName(ByteCodeGenUtil.getFullyQualifiedClassName(model), true,
					this.getClass().getClassLoader());
			assertNotNull(clazz);
			Field field = clazz.getDeclaredField("loadedClazz");
			assertNotNull(field);
			assertEquals(Class.forName(ByteCodeGenUtil.getFullyQualifiedClassName(assessmentFormModel)), field.getType());
		} catch (ClassNotFoundException e) {
			assertTrue(false);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}

	/*
	 * Test case to verify agent is able to create dynamically created class as an attribute to another dynamically created class. 
	 * This test case verifies,  private LoadedClazz loadedClazz; attribute will be created in ShoppingCart.class.
	 */
	@Test
	public void testNestedModelAttribute() {

		ModelDetail model = getModel();

		AttributeDetail attrDetail = new AttributeDetail();
		attrDetail.setName("loadedClazz");
		attrDetail.setType(NESTED_MODEL);
		attrDetail.setId(1L);
		model.setAttributes(Arrays.asList(attrDetail));

		publisher.publishModel(model);

		try {
			Class<?> clazz = Class.forName(ByteCodeGenUtil.getFullyQualifiedClassName(model), true,
					this.getClass().getClassLoader());
			assertNotNull(clazz);
			Field field = clazz.getDeclaredField("loadedClazz");
			assertNotNull(field);
			assertEquals(Class.forName(model.getPackageName() + ".LoadedClazz"), field.getType());
		} catch (ClassNotFoundException e) {
			assertTrue(false);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}


	/*
	 * Test case to verify agent is able to create dynamically created complex class as an attribute to another dynamically created class.
	 *
	 * 	class ShoppingCart{
	 * 		private Item item;
	 * }
	 * 	class Item{
	 * 		private String itemName;
	 * 	}
	 *
	 * This test case verifies above classes are generated for the given metadata in test case.
	 */
	@Test
	public void testNestedModelAttributeWithAttributes() {

		ModelDetail shoppingCart = getModel();

		AttributeDetail itemDetail = new AttributeDetail();
		itemDetail.setId(1L);
		itemDetail.setName("item");
		itemDetail.setType(NESTED_MODEL);

		AttributeDetail itemNameDetail = new AttributeDetail();
		itemNameDetail.setName("itemName");
		itemNameDetail.setType(STRING);
		itemNameDetail.setParentId(itemDetail.getId());

//		itemDetail.setAttributes(Arrays.asList(itemNameDetail));
		shoppingCart.setAttributes(Arrays.asList(itemDetail, itemNameDetail));

		publisher.publishModel(shoppingCart);

		try {
			Class<?> clazz = Class.forName(ByteCodeGenUtil.getFullyQualifiedClassName(shoppingCart), true,
					this.getClass().getClassLoader());
			assertNotNull(clazz);
			Field field = clazz.getDeclaredField("item");
			assertNotNull(field);
			assertEquals(Class.forName(shoppingCart.getPackageName() + ".Item"), field.getType());
			clazz = Class.forName(shoppingCart.getPackageName()+".Item", true,
					this.getClass().getClassLoader());
			field = clazz.getDeclaredField("itemName");
			assertNotNull(field);
			assertEquals(String.class, field.getType());
		} catch (ClassNotFoundException e) {
			assertTrue(false);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}

	/*
	 * Test case to verify agent is able to create predefined java types as attributes for dynamically generated classes. 
	 * This test case verifies, private String email; attribute will be created in ShoppingCart.class.
	 */
	@Test
	public void testStandardAttribute() {

		ModelDetail model = getModel();

		AttributeDetail attrDetail = new AttributeDetail();
		attrDetail.setName("email");
		attrDetail.setType(STRING);
		model.setAttributes(Arrays.asList(attrDetail));

		publisher.publishModel(model);

		try {
			Class<?> clazz = Class.forName(ByteCodeGenUtil.getFullyQualifiedClassName(model), true,
					this.getClass().getClassLoader());
			assertNotNull(clazz);
			Field field = clazz.getDeclaredField("email");
			assertNotNull(field);
			assertEquals(String.class, field.getType());
		} catch (ClassNotFoundException e) {
			assertTrue(false);
		} catch (Exception e) {
			assertTrue(false);
		}

	}

	private ModelDetail getModel() {

		ModelDetail model = new ModelDetail();
		model.setPackageName(AppConstants.DEFAULT_BASE_PACKAGE.code);
		model.setId(version.getAndIncrement());
		model.setName("ShoppingCart" + model.getId());
		return model;
	}


	public static class LoadedClazz {

	}

}
