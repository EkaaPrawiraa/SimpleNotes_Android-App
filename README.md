# Simple Notes Application

The Simple Notes application is a simple note-taking app that allows users to create, edit, and manage their notes easily. The app also supports importing notes from Excel files (.xls and .xlsx) and searching for notes using string matching algorithms.

## How to Use the Application

1. **Create a Note:**
   - Open the application and select the option to add a new note.
   - Fill in the title, category, and content of the note.
   - Click the "Save" button to save the note.

2. **Edit a Note:**
   - Select the note you want to edit from the list of notes.
   - Modify the required information.
   - Click the "Save" button to update the note.
3. **Delete a note:**
   - Long click the note you want to delete from the list of notes.
   - You still can undo the deletion for several seconds if you click the "undo" button.


4. **Import Notes from Excel:**
   - Click the "Add" button or select the note you want to edit.
   - Click the "Import File" button.
   - Select the Excel file containing the notes.
   - The notes will be imported and displayed in the appropriate fields.
     ```bash
     The first cell will be the "Title"
     The second cell will be the "Category"
     The third cell will be the "Content"
      ```

5. **Search for Notes:**
   - Use the search feature to find notes based on title or content.
6. **Filter for Notes:**
   - Use the filter feature to filter notes base on category.

## Sorting Algorithm Selection

This application uses two sorting algorithms: **Merge Sort** and **Quick Sort**. Here is a comparison of both:

- **Merge Sort:**
  - A divide and conquer-based sorting algorithm.
  - Has a time complexity of O(n log n) in all cases.
  - Stable (does not change the order of elements with equal values).
  - Requires additional space to store a temporary array.

- **Quick Sort:**
  - Also a divide and conquer-based sorting algorithm.
  - Has an average time complexity of O(n log n) but can degrade to O(n²) in the worst-case scenario (depending on the pivot selection).
  - Not stable.
  - Performs exceptionally well in practice due to minimal space requirements.

**Conclusion:** 
Merge Sort is more stable and has consistent performance, while Quick Sort is generally faster in practice, even though it is not stable.

## String Matching Algorithm

This application uses two string matching algorithms: **Knuth-Morris-Pratt (KMP)** and **Boyer-Moore**. Here is a comparison of both:

- **Knuth-Morris-Pratt (KMP):**
  - Utilizes information obtained from previous searches to avoid checking characters that are already known.
  - Has a time complexity of O(n + m), where n is the length of the text and m is the length of the pattern.
  - Effective for long patterns and repetitive text.

- **Boyer-Moore:**
  - Makes use of information about the pattern to shift the search further when there is a mismatch.
  - Has an average time complexity of O(n/m) but O(n * m) in the worst-case scenario.
  - Highly efficient for shorter patterns compared to longer texts.

**Conclusion:** 
KMP is more efficient for processing long texts with complex patterns, while Boyer-Moore is more efficient for searching short patterns within longer texts.

## Application Screenshots



### Homepage
![Homepage](img/Screenshot_20240812_060114.png)

### Sorting Feature
![Sorting Feature](img/Screenshot_20240812_060238.png)

### Filtering Feature
![Filtering Feature](img/Screenshot_20240812_060306.png)

### Searching Feature
![Searching Feature](img/Screenshot_20240812_060337.png)

### Settings Page
![Settings Page](img/Screenshot_20240812_060349.png)

### Deletion & Undo Feature
![Deletion & Undo Feature](img/Screenshot_20240812_060403.png)

### Add & Edit Note Page
![Add & Edit Note Page](img/Screenshot_20240812_060413.png)

### Import File Feature
![Import File Feature](img/Screenshot_20240812_060423.png)

### Imported File to Note
![Imported File to Note](img/Screenshot_20240812_060434.png)



## Minimum Device Specifications

- **OS:** Android 5.0 (Lollipop) or higher
- **RAM:** Minimum 2 GB
- **Storage:** Minimum 50 MB of free space
- **Processor:** Dual-core 1.2 GHz or higher
