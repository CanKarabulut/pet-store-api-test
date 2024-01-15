package com.insider.apitest;
import com.insider.apitest.dto.*;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.*;

import static io.restassured.RestAssured.*;

public class PetTests extends BaseTest{

    //1. uploads an image
    @Test
    public void shouldUploadAnImage() {
        int petId = 77770 ;
        String imageFilePath = "cat.jpg";
        File imageFile = new File(imageFilePath);
        var response = given()
                .header("api_key",API_KEY)
                .contentType(ContentType.MULTIPART)
                .multiPart("additionalMetadata", "canTest")
                .multiPart("file", imageFile, "image/jpeg")
                .post(baseURI + "/" + petId + "/uploadImage");
        Assert.assertEquals(response.getStatusCode(), 200);
        System.out.println(response.prettyPrint());

        Response responseMap = response.as(Response.class);
        Assert.assertEquals(responseMap.getCode(), 200);
        Assert.assertEquals(responseMap.getMessage(), "additionalMetadata: canTest\nFile uploaded to ./cat.jpg, 103061 bytes");
    }

    //2. Add a new pet to the store
    @Test
    public void shouldAddNewPetToStore() {
        int petId = 77770;
        String petName = "ceku";
        int categoryId = 177;
        String categoryName = "cats";
        String status = "available";
        String photoUrls = "https://as1.ftcdn.net/v2/jpg/02/04/02/94/1000_F_204029461_ZWs6BCfe68elXjEwtTB2yBeohuzhdvC2.jpg";
        int tagId = 77770;
        String tagName = "cat-photo";

        Pet petMap = new Pet();
        petMap.setId(petId);
        petMap.setName(petName);
        petMap.setStatus(status);

        Category newCategory = new Category();
        newCategory.setId(categoryId);
        newCategory.setName(categoryName);
        petMap.setCategory(newCategory);

        List<String> photoUrlsMap = new ArrayList<>();
        photoUrlsMap.add(photoUrls);
        petMap.setPhotoUrls(photoUrlsMap);

        List<Tag> tagList = new ArrayList<>();

        Tag tag = new Tag();
        tag.setId(tagId);
        tag.setName(tagName);

        tagList.add(tag);

        petMap.setTags(tagList);

        var response = given()
                .header("api_key", API_KEY)
                .contentType(ContentType.JSON)
                .body(petMap)
                .post(baseURI);
        System.out.println(response.prettyPrint());

        Pet pet = response.as(Pet.class);
        Assert.assertEquals(pet.getId(), petId);
        Assert.assertEquals(pet.getName(), petName);
        Assert.assertEquals(pet.getStatus(), status);

        Assert.assertEquals(pet.getCategory().getId(),categoryId);
        Assert.assertEquals(pet.getCategory().getName(),categoryName);

        for (String photoUrl : pet.getPhotoUrls()){
            Assert.assertEquals(photoUrl, "https://as1.ftcdn.net/v2/jpg/02/04/02/94/1000_F_204029461_ZWs6BCfe68elXjEwtTB2yBeohuzhdvC2.jpg");
        }

        for (Tag tags : pet.getTags()) {
            Assert.assertEquals(tags.getId(),tagId);
            Assert.assertEquals(tags.getName(),tagName);
        }

    }

    //3. Update an Existing Pet
    @Test
    public void shouldUpdateExistingPet() {
        int petId = 77771;
        String petName = "ceku";
        int categoryId = 177;
        String categoryName = "cats";
        String status = "available";
        String photoUrls = "https://as1.ftcdn.net/v2/jpg/02/04/02/94/1000_F_204029461_ZWs6BCfe68elXjEwtTB2yBeohuzhdvC2.jpg";
        int tagId = 77770;
        String tagName = "cat-photo";

        Pet petMap = new Pet();
        petMap.setId(petId);
        petMap.setName(petName);
        petMap.setStatus(status);

        Category newCategory = new Category();
        newCategory.setId(categoryId);
        newCategory.setName(categoryName);
        petMap.setCategory(newCategory);

        List<String> photoUrlsMap = new ArrayList<>();
        photoUrlsMap.add(photoUrls);
        petMap.setPhotoUrls(photoUrlsMap);

        List<Tag> tagList = new ArrayList<>();

        Tag tag = new Tag();
        tag.setId(tagId);
        tag.setName(tagName);

        tagList.add(tag);

        petMap.setTags(tagList);

        var response = given()
                .header("api_key", API_KEY)
                .contentType(ContentType.JSON)
                .body(petMap)
                .put(baseURI);
        Assert.assertEquals(response.getStatusCode(), 200);
        System.out.println(response.prettyPrint());

        Pet pet = response.as(Pet.class);
        Assert.assertEquals(pet.getId(), petId);
        Assert.assertEquals(pet.getName(), petName);
        Assert.assertEquals(pet.getStatus(), status);

        Assert.assertEquals(pet.getCategory().getId(),categoryId);
        Assert.assertEquals(pet.getCategory().getName(),categoryName);

        for (String photoUrl : pet.getPhotoUrls()){
            Assert.assertEquals(photoUrl, "https://as1.ftcdn.net/v2/jpg/02/04/02/94/1000_F_204029461_ZWs6BCfe68elXjEwtTB2yBeohuzhdvC2.jpg");
        }

        for (Tag tags : pet.getTags()) {
            Assert.assertEquals(tags.getId(),tagId);
            Assert.assertEquals(tags.getName(),tagName);
        }
    }

    //4. Find Pets by Status
    @Test
    public void shouldGetAllPetsByStatus() {
        String status = "sold";
        var response = given()
                .header("api_key", API_KEY)
                .queryParam("status", status)
                .get(baseURI + "/findByStatus");
        Assert.assertEquals(response.getStatusCode(), 200);

        System.out.println(response.prettyPrint());

        List<Map<String, Object>> items = response.jsonPath().getList("");

        for (Map<String, Object> item : items) {
            Assert.assertEquals(item.get("status"), status);
        }
    }

    //5. Find By Pet Id
    @Test
    public void shouldGetPetByIdSuccessfully() {
        int petId = 77770;
       var response = given()
                .header("api_key",API_KEY)
                .get(baseURI + "/" + petId);

        Assert.assertEquals(response.getStatusCode(), 200);

        Pet pet = response.as(Pet.class);
        Assert.assertEquals(petId, pet.getId());
        Assert.assertEquals("ceku", pet.getName());
        Assert.assertEquals("available", pet.getStatus());

        Assert.assertEquals(pet.getCategory().getId(), 177);
        Assert.assertEquals(pet.getCategory().getName(), "cats");

        Assert.assertNotNull(pet.getPhotoUrls());

        for (Tag petTag : pet.getTags()) {
            Assert.assertEquals(petTag.getId(), 77770);
            Assert.assertEquals(petTag.getName(), "cat-photo");
        }
        System.out.println(response.prettyPrint());
    }

    //5.1 Check the Exception Message for Wrong Pet Id
    @Test
    public void shouldNotGetPetWrongId() {
        int petId = 7;
        io.restassured.response.Response response;
        try {
            response = given()
                    .header("api_key",API_KEY)
                    .get(baseURI + "/" + petId);
        }catch(Exception e) {
            Assert.assertEquals(e.getMessage(), "status code: 404, reason phrase: Not Found");
        }
    }


    //5.2 Check the Error Message for Minus Value
    @Test
    public void shouldNotGetMinusPetId() {
    int petId = -1;
    io.restassured.response.Response response = null;
        try {
        response = given()
                .header("api_key",API_KEY)
                .get(baseURI + "/" + petId);
    }catch(Exception e) {
        Assert.assertEquals(e.getMessage(), "status code: 404, reason phrase: Not Found");
    }
}
    //6. Updates a pet in the store with form data
    @Test
    public void shouldUpdatePetWithFormDataValues() {
        int petId = 77770;
        String updatedPetName = "cekus";
        String updatedPetStatus = "pending";
       var response = given()
                .header("api_key",API_KEY)
                .formParam("name",updatedPetName )
                .formParam("status", updatedPetStatus)
                .post(baseURI + "/" + petId);
        Assert.assertEquals(response.getStatusCode(), 200);

        System.out.println(response.prettyPrint());

        Response petResponse = response.as(Response.class);
        Assert.assertEquals(petResponse.getCode(), response.getStatusCode());
        Assert.assertNotNull(petResponse.getType());
        Assert.assertEquals(petResponse.getMessage(), "77770");
    }

    //7. Deletes a pet
    @Test
    public void removePetById() {
        int petId = 77770;
        var response = given()
                .header("api_key",API_KEY)
                .delete(baseURI + "/" + petId);

        Assert.assertEquals(response.getStatusCode(), 200);
        System.out.println(response.prettyPrint());

        Response petResponse = response.as(Response.class);
        Assert.assertEquals(petResponse.getCode(), response.getStatusCode());
        Assert.assertNotNull(petResponse.getType());
        Assert.assertEquals(petResponse.getMessage(), "77770");
    }
}