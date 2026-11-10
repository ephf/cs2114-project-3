<div align="center">
    <h1>CS2114 - Project 3 - Hold Your Spot</h1>
</div>

We are building a digital restaurant waitlist manager that can be used by hosts and desk managers. It will be their way 
of managing waiting customers during busy service hours. They will be able to add walk-in parties to a waitlist, view 
who's next in line, assign parties to open tables when they become available, and update party details or remove parties 
if they leave. This application gives staff a clear overview of who is waiting and how long the line is, helping run things 
efficiently without losing track of guests or seating people out of order.

> [!IMPORTANT]
> Source files located in [src/com/holdyourspot/](src/com/holdyourspot)

# Reflection

## What changed from the specification?

A good amount changed in our classes. We originally planned to use WaitlistEvent and WaitlistListener, but we didn't implement them because we found an easier way to do it. We also renamed a few classes, used a generic Queue<T>, and haven't implemented the ticket ID system yet. Some methods and exceptions were also changed so that each scenario throws a more appropriate exception.

## Did the model really stand alone?

Mostly. Queue<T> has no System.out or Scanner. However, the model, controller, and view lines all moved into PartyManager, which runs the prompts and also contains the domain rule of matching names.

## What happened to our unknowns?

We figured out how to make mid-queue removal work by using a prev pointer in removeIf. We also found a solution for duplicate names using name-prefix matching, which turned out to be simpler to implement than ticket IDs.

## What doesn't work yet?

We haven't implemented the view-next-to-be-seated feature, which is part of our MVP. We also need to add bad-input handling for blank party names, which are currently accepted. Duplicate-name confirmation and the help feature still need to be added as well.


# Next Steps

## What is left to finish the MVP?

1. Fix the bug in removeIf. We need to update back when the removed node is the tail.
2. Implement the view-next-to-be-seated feature, which should be fairly simple using peek().
3. Finish the smaller items: blank party name validation, duplicate-name confirmation, the help feature, and polishing console messages.

## Change feature: Seating History

We will add a seating history that shows the last N parties seated, to better manage exactly who is at a table. It will touch the PartyManager class by adding a new option to it, and we will create a new class called History to store the records.

Prediction:
- PartyManager will change (new menu option).
- History will be a new class.
- Queue, Party, and Main should not need to change.

## Stretch goals

Attempting:
- Waitlist history. We think it will be a valuable feature for the app.
- No-show handling, if we have time. This covers a party that doesn't show up when their name is called.

Dropping:
- Seating by table size, since it conflicts with our FIFO design.
- Wait time calculator, since we haven't resolved the best way to estimate the time and it would need to be personalized for each restaurant.

## New tests needed

- View next to be seated, once it's implemented
- Blank name validation
- Duplicate-name confirmation
- Waitlist history, including an empty history and making sure everything is recorded correctly for removals and entries

## Who does what?

- Nate: waitlist history feature
- Luci: no-show handling
- Grant: bug fixes and the view-next-to-be-seated feature
- Tia: tests for everything, and help with new bugs

## Work attribution
- Main: Grant, Luci
- Party: Luci, Nate, Grant
- PartyManager: Nate, Grant
- PromptDisplay: Grant, Luci
- Queue: Grant
- MainTest: Tia, Luci
- PartyTest: Tia, Grant
- PartyManagerTest: Tia, Grant
- PromptDisplayTest: Tia, Nate
- QueueTest: Tia

