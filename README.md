# evilnotchlib
this is a library with minecraft and some forge bug fixes. It allows modders to mod with ease and a relection MCPMappings Api to allow for reflection setting and getting objects and set final objects as well. This is a powerful lightweight library

Embeded Libraries:
```
Simple JSON(With Modifications): https://github.com/fangyidong/json-simple
```

Bug Fixes:
```
vanilla eggs on spawners
uuid fix(if uuid doesn't match server patch it)
uuid fix single player(if you give another person your world you get the right playerdata)
fixes forge parsing playerdata files twice on login
TileEntityFurnace(increase from short to int,fixed not properly reading/writing currentItemBurnTime)
GuiFurnace(fixed gui going out of bounds of integer thus displaying data wrong)
notch drops apples again
ItemToolTip Enchantment Fix(if text breaks manually uses enchantment name and roman numeral generator or integer)
BlockEntityTag fix(sync client changes on the same tick rather then later,don't have to be in creative)
future: Move too quickly and vechile checks removed since this is a modding enviorment
```

Features:
```
MCPMappings and ReflectionUtil API: ability to get and set objects with ease
Menu Lib: allows for modders to register their menu for multiple menu browsing
BlockAPI: set objects in blocks for coders
Player Capabilities: different from forge they are easy to regiser and use. Currently server side only
Basic MC Lib:ability to make modding easier automation for registration and lang
GeneralRegistry: registry for commands, sound types and other general stuffs regsiter stuff here for compatibility
EntityModRegistry: support SpawnListEntries with NBT mobs
Client Block Place Event: fires on client side for client sync on block placing
Line Library: a powerful library for parsing lines in many forms "modid:block" = "custom parsing values"
ConfigBase: part of the line library as an api implementation for the line library
Primitive Obj: allows for object modifyable primitive values(byte,short,int,long,float,double,boolean)
PairObj: unlike the other Pair classes this one makes since and uses generics so you never have to type cast
JavaUtil: varius pure java utilities
CSVE: basic implementation of comma seperated values with a varible system in place
Json model generation(Basic MC Lib)
Lang generation(Basic MC Lib)

Future:
Font Renderer
NBTPathAPI
Dynamic Client Translations
Lan Skin Host Fix
```

Instalation Compiled:
```
Install forge mdk mc version
Create a libs folder and put the version of EvilNotch Lib you need
Go into eclipse java build path and add the jar
Installl the build.gradle to your mdk as it has support for UTF-8 compiliation
Go into EvilNotLib jar > src > main > resources > evilnotchlib > asm > decompiled
Drag and drop what patched decompiled classes you need and rename the compilations and move to the proper packaging
Sadley this is the only way in the lib to setup the decomp work space since the asm isn't written for decompiled yet. They are all patched classes
When compiling your mod make sure you delete the net/minecraft folder as it's not needed because in compiled it's done using asm
```
