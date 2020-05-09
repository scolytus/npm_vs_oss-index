# Debug helper for Step 4

If you want to see progress in Step 4 this can be used in the debugger:

```
final List<String> temp = new ArrayList<>(allReferences);
for (int i=100; i < temp.size(); i++) {
    if(temp.get(i).contains("1cc")) {
        System.out.println("" + i + ": " + temp.get(i));
    }
}
```
