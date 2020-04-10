This package contains VorIonItem class and VorIonFactory + VorIonFactoryBuilder

## What is the problem it is trying to solve?


VorIonItem is a wrapper around IonJava implementation that allows 
simple navigation and modification of its data hierarchy.

Using IonJava library is sometimes very tedious - e.g. 
given very simple structure: 
```
{
   data:{
        item:"text"
   }
}
```

`IonJava` code to set is would be:
```
IonStruct root = ionSystem.newEmptyStruct();
IonStruct data = ionSystem.newEmptyStruct();
data.put("item", ionSystem.newString("text");
root.put("data", data);
``` 
With `VorIonItem` you can replace it with:
```
VorIonItem root = VorIonFactory.empty();
root.set("data/item", "text");
```

Read operations are even more complex. In order to read this data using `IonJava` you need to create function like:
```
Optional<String> readItem(IonValue root) {
  if (root != null && IonType.STRUCT.equals(root.getType)) {
    IonStruct struct = (IonStruct) root;
    IonValue data = struct.get(data);
    if (data != null && IonType.STRUCT.equals(data.getType)) {
      IonStruct dataStruct = (IonStruct) data;
      IonValue item = dataStruct.get("item");
      if (item != null && IonType.isText(item.getType())) {
        IonText itemText = (IonText) item;
        return Optional.of(itemText.stringValue()); 
      } 
    }
  }
  return Optional.empty()
}

```
Whereas with `VorIonItem` you can replace it with:
```
VorIonItem root = VorIonFactory.from(ionValue);
return root.get("data/item").optionalString();
```
 
## VorIonItem 

#### Tenets:
* VorIonItem class is a **single type** that supports all Ion data types (LIST, STRUCT, STRING, ...)
* data access functions **always return non-null values** even, if underlying data doesn't exist (*)   
* data navigation can use chained sequence or when always accessing same set it can be further simplified to path:
  * root.get("data").get("list").get(2).get("list_element").get("value").getS()
  * root.get("data/list:2/list_element/value").getS()
  * (alternative path notation) root.get("data.list[2].list_element.value").getS()
* data navigation methods **never change** underlying ion data.
* minimize performance penalty by dynamically creating Objects for accessed elements only - if ion structure contains 
hundreds of nodes, and we access only few of them only few VorIonItem instances will be 
created - all data is sourced from ion structure.


(*) System leniency can be set by lenientStructure flag in VorIonFactoryBuilder
 * lenientStructure == true => allow any data reads/writes  
 * lenientStructure == false => enforce data type validation - you cannot change type of data or access **existing** data as other type (e.g. treat list as struct).
 
 ##  VorIonFactory 
 
VorIonFactory is the only mechanism to create instance of VorIonItem.

VorIonFactory itself must be created using VorIonFactoryBuilder that
allows setting path parser format ("a/b/c:n" vs "a.b.c[n]"), IonSystem to be used,
and IonElement behaviour (leniency flags)

## VorIonFactoryBuilder

VorIonFactoryBuilder allows you to build VorIonFactory.
