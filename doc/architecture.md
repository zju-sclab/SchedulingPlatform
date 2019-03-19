##The whole project Architecture

### Convention

    ```
    repository: data access layer, dal is suitable.
    
    model: entity and value object.
    
    common: place some exception classes, functions, enumerations and so on.
    
    infrastructure: dependency other modules, or projects, always means external services.
    
    core: the main concept or operations in this module.
    
    service: provided operations for external components or systems.
    ```
### introduction for different modules
    
    **common**
    ```
    common configurations 
    ```
    
    **dal**
    ```
    will merged to common module in the near future.
    ```
    
    **carmanager**
    ```
    this module is mainly designed for monitor the ros system,
    and do somethings like start ros and collect data.
    ```
    
    **map**
    ```
    map stores different regions' net and link-shape.
    The core is RegionNet class.
    Route and trajectory is generated in this module.
    ```
    
    **autonomous**
     ```
     this module is mainly designed for move car from a to b.
     TrajectoryTask is the main concept.
     
     ```
    
    **scheduler**
    ```
    this module is mainly designed for scheduled the car in traffic net.
    The core module haven't been implemented yet.
    For schedule pupose, route is needed.
    
    this module depends on map and autonomous module. 
    This moudle provides route-level autoTask.
    ```

    **web**
    ```
    the interactive module.
    ```