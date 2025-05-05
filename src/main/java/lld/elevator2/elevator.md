```mermaid
classDiagram
    class Elevator {
        +String id
        +Door door
        +ElevatorState state
        +int currentFloor
        +ElevatorPanel elevatorPanel
        +Display elevatorDisplay
        +move()
        +stop()
        +openDoor()
        +closeDoor()
    }
    class ElevatorState {
        <<interface>>
        +move(Elevator)
        +stop(Elevator)
        +openDoor(Elevator)
        +closeDoor(Elevator)
    }
    Elevator --> ElevatorState
    Elevator --> Door
    Elevator --> ElevatorPanel
    Elevator --> Display