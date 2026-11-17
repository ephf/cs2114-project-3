# Hold Your Spot

**Team:** Luci, Nate, Grant, Tia

**CS 2114 · Project 3 · Deliverable 4 — Second Prototype + Tests**

Hold Your Spot is a Java console waitlist manager for restaurant hosts. Parties join the back of a single first-come, first-served waitlist and are seated from the front. The waitlist is stored in a custom generic singly linked `Queue<T>` that tracks `front`, `back`, and `size`.

## Running the Project

- **Entry point:** `com.holdyourspot.Main`
- **Java:** JDK 22 or newer (the code uses unnamed `_` lambda and catch parameters).
- **Tests:** JUnit 5, in `unit_tests/`. Run the whole folder from IntelliJ (right-click `unit_tests` → *Run Tests*).

Menu options once the program is running:

| Key | Action                                                   |
|-----|----------------------------------------------------------|
| `a` | Add a party (name, then size)                            |
| `s` | Seat the next party                                      |
| `v` | View the full waitlist                                   |
| `n` | View who is next to be seated, without removing them     |
| `h` | View seating history (**new this checkpoint**)           |
| `r` | Remove a party by name (prefix search, with y/n confirm) |
| `q` | Quit                                                     |

## Design

| Layer      | Classes                                                                            | Role                                                                       |
|------------|------------------------------------------------------------------------------------|----------------------------------------------------------------------------|
| Model      | `Party`, `Queue<T>`, `Waitlist`, `WaitlistEvent`, `WaitlistListener`, `History<T>` | FIFO order, ticket IDs, validation, change events, seating history         |
| Controller | `WaitlistController`                                                               | Owns the command loop, reads choices, calls the model, registers listeners |
| View       | `ConsoleView`, `GridPrinter`                                                       | Menu grid, prompts, retry-until-valid input, error text                    |
| Bootstrap  | `Main`                                                                             | Creates `WaitlistController` and starts the command loop                   |

**How the pieces connect**

- `Waitlist` is the only class that touches the `Queue<Entry>`. Each `Entry` pairs a `Party` with a never-reused ticket ID.
- `Waitlist` fires a `WaitlistEvent` (`ADDED`, `SEATED`, `REMOVED`) to every registered `WaitlistListener` only after a change succeeds. Events carry the type, the new waiting count, the ticket ID, and the party.
- `History<T>` is a fixed-capacity log built on a second `Queue<T>`. When it is full, recording a new item drops the oldest one. It knows nothing about `Waitlist`; the controller connects them with a listener that records every `SEATED` event.
- `WaitlistController` creates one `Waitlist` and one `History<Party>` (capacity 10), then loops: show the count, get a menu choice from `ConsoleView`, call the model, print the result.

**Complexity.** `enqueue`, `dequeue`, `peek`, `isEmpty`, and `size` are O(1). `removeIf`, `enumerate`, and everything built on them in `Waitlist` (`removeParty`, `searchForName`, `getWaitingParties`) are O(n). `History.record` is O(1); `History.enumerate` is O(n).

## Reflection

### How did your design absorb the change?

**Feature: Seating History.** The host can open a list of the last 10 parties that were seated. This is the change we named in our Deliverable 3 Next Steps.

- **Touched:**
  - `History` (new): a generic, fixed-capacity log built on `Queue<T>`.
  - `WaitlistController`: creates the `History<Party>`, registers a `WaitlistListener` that records `SEATED` events, and adds the `h` menu case.
  - `ConsoleView`: one new `Option` entry for "View Seating (H)istory".
- **Not touched:** `Queue`, `Party`, `Waitlist`, `WaitlistEvent`, `WaitlistListener`. `History` reuses `Queue` without changing it.
- **vs. the Deliverable 3 prediction:** We predicted that the controller would change, `History` would be new, and `Queue`, `Party`, and `Main` would stay untouched. That matched, except that `ConsoleView` also needed its one-line menu entry. The surprise was how cheap the feature was: it was cheap only because `WaitlistEvent` and `WaitlistListener` already existed. In the earlier prototype the old `PartyManager` used `Queue<Party>` directly, so the model never announced anything and nothing could listen. The real cost of the change was the earlier work of routing every add and seat through `Waitlist`, not the history code itself.

### Did the change cross the model/view line?

Barely. `ConsoleView` learned only that there is a new menu option. It does not know that history exists, how `History` stores records, or that it listens to `Waitlist`, and `Queue` internals never reach the view. The new feature needed no changes to `Waitlist`, so no rule moved between layers.

Two places where the line is still blurry, which we want to fix before the final build:

- `WaitlistController` prints list, next, seat, and history output itself with `System.out.printf` instead of asking `ConsoleView` to print. The history output is the newest example of this.
- In the remove flow, the controller passes `Waitlist.removePartyIf` a predicate that prompts the user (y/n) from inside the model call. It works, but the model call is doing user interaction, and it bypasses the `REMOVED` event (see below).

### What changed from the Deliverable 3 plan?

- **Structure:** We kept the generic `Queue<T>` and added `ConsoleView`, `GridPrinter`, and `WaitlistController`. `Main` now only starts the controller loop.
- **Queue methods:** We implemented `removeIf` and `enumerate` instead of `remove(ticketId)`, `findByName`, and `toList`. `Waitlist` builds those operations on top, which keeps restaurant rules out of the queue.
- **Ticket IDs:** They live in `Waitlist`'s private `Entry` (never reused), not in `Party`.
- **Validation:** Name and size checks are in `Waitlist.addParty` (and `Waitlist.validateName`), not in `Party`. `Party` is a plain record, so `PartyTest` documents that it accepts an empty name, a `null` name, and a negative size. The `Waitlist` tests cover the real validation.
- **Name search:** The model method is `searchForName` (case-insensitive, matches names that *start with* the search text, FIFO order), plus `hasPartyNamed`, rather than a `findByName` method.
- **Events:** `WaitlistEvent` also carries the ticket ID, and `WaitlistListener` has a single method, `onWaitlistEvent`.
- **Removing a party:** The model supports removal by ticket ID (`removeParty`), but the console currently removes by name prefix with a y/n confirmation (`removePartyIf`), because ticket numbers are not shown yet.
- **History:** We record only seated parties (not removed ones), the log is generic (`History<T>`), and it prints oldest to newest.
- **Tests:** Normal, bad-input, and edge tests exist for `Party` (6), `Queue` (13), `Waitlist` (W1–W18 plus a tail-removal regression), and `History` (6).

### What doesn't work yet?

- **`Queue.removeIf` bug when the queue has exactly one element.** Removing the only element sets `front` to `null` but leaves `back` pointing at the removed node, so the next `enqueue` is lost (`size` says 1, but `isEmpty()` is true). The existing regression test only covers removing the tail of a two-element queue, so no test catches this yet.
- **Removing through the console does not fire a `REMOVED` event**, because it uses `removePartyIf`. As a result, removed parties cannot show up in history, and answering "n" to every match shows "Cannot find a party" even though a match existed.
- **Duplicate-name confirmation is not implemented.** The model support (`hasPartyNamed`) exists and is tested, but the controller does not use it.
- **Help option is not implemented.**
- **Ticket numbers are not shown** in the waitlist view or the remove flow, so two parties with the same name are hard to tell apart.
- **History ordering:** the list prints oldest first. 
- **Missing tests:** `Waitlist.removePartyIf`, `Waitlist.enumerate`, and the controller's history listener have no tests, and there is no test for removing the only element of a `Queue`.
- **End of input:** if standard input is closed, `promptUntilValid` retries forever because it catches every exception.

## Next Steps

### What is left for the final build?

In order:

1. **Fix the size `0` crash.** Make `getPositiveInteger` reject `0`, and have the controller catch `IllegalArgumentException` from `Waitlist.addParty`.
2. **Fix `Queue.removeIf` for a single-element queue** (reset `back` to `null` when `front` becomes `null`) and add a `QueueTest` for it.
3. **Route removal through `Waitlist.removeParty(ticketId)`** so a `REMOVED` event is fired, and **show ticket numbers** in the waitlist view and the remove flow.
4. **Duplicate-name confirmation** in the add flow, using `Waitlist.hasPartyNamed`.
5. **Help option** listing every menu choice.
6. **History polish:** show most recent first (or label the order), decide whether removed parties are recorded, and handle an empty history cleanly.
7. **Fill the test gaps** listed above, then do message polish and a final full test run.

### Which stretch goals are you committing to, and which are you dropping?

| Stretch goal              | Decision                       | Notes                                                                                                                                                                                                                                                                                                  |
|---------------------------|--------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Waitlist history          | **Done (this checkpoint)**     | This was our first stretch goal and became the new feature we built for this deliverable. Remaining work is polish (item 6 above).                                                                                                                                                                     |
| No-show handling          | **Committing, if time allows** | A host can mark a party as a no-show and remove it from the list. The plan is a `Waitlist` method that removes by ticket and fires an event, plus a menu option. It builds on the same ticket-ID removal we are already fixing in item 3, so it should touch `Waitlist`, `WaitlistEvent`, and the controller/view menu, not `Queue`. We start it only after items 1–6 are finished and tested. |
| Seating by table size     | **Dropping**                   | Conflicts with the strict first-come, first-served rule at the core of the design.                                                                                                                                                                                                                     |
| Wait-time calculator      | **Dropping**                   | Needs restaurant-specific data (turn times, table counts) that we do not model.                                                                                                                                                                                                                        |
| Table assignment tracking | **Dropping**                   | Keeps the final scope small and focused on the waitlist itself.                                                                                                                                                                                                                                        |

### What will your 3-minute demo show?

1. Start with an empty list and try **next** (`n`) and **seat** (`s`): friendly messages, no crash.
2. **Add** (`a`) three parties, including a blank name, a size of `abc`, and a size of `0` (each re-prompts instead of crashing), and a duplicate name (confirmation prompt).
3. **View** the waitlist (`v`) with ticket numbers, then view next (`n`) without removing anyone.
4. **Remove** (`r`) a party from the middle and view the waitlist again to show the order of the others is unchanged.
5. **Seat** (`s`) two parties, then open **Seating History** (`h`), the new feature, and show that it lists the seated parties. Show the empty-history message in a fresh run if time allows.
6. Show the **help** option, run the JUnit suite, then quit (`q`).
7. If the stretch goal is finished: mark a party as a no-show between steps 4 and 5.

### Who does what?

| Item                                                                            | Owner       |
|---------------------------------------------------------------------------------|-------------|
| Size `0` crash fix, ticket numbers in the view, removal through `removeParty`   | Grant       |
| Duplicate-name confirmation                                                     | Grant       |
| `Queue.removeIf` single-element fix and its test                                | Luci        |
| History polish (ordering, empty case, removed parties) and help option          | Nate        |
| No-show handling (stretch, only if items above are done)                        | Luci, Nate  |
| Test gaps (`removePartyIf`, `enumerate`, history listener), final full test run | Tia         |
| Message polish, demo script, rehearsal                                          | Grant, Nate |

## Attribution

| Class or test file        | Team member(s)    | Contribution                                           |
|---------------------------|-------------------|--------------------------------------------------------|
| `Main.java`               | Grant, Luci       | Wrote, revised                                         |
| `Party.java`              | Luci, Nate, Grant | Wrote (record), reviewed                               |
| `Queue.java`              | Grant             | Wrote; fixed `removeIf` tail case                      |
| `Waitlist.java`           | Tia, Luci         | Wrote (ticket IDs, events, validation); GenAI-assisted |
| `WaitlistEvent.java`      | Tia               | Wrote                                                  |
| `WaitlistListener.java`   | Tia, Luci         | Wrote                                                  |
| `History.java`            | Nate              | Wrote; GenAI-assisted                                  |
| `WaitlistController.java` | Nate, Grant       | Wrote, revised, rewired to `Waitlist`                  |
| `ConsoleView.java`        | Grant, Luci       | Wrote, reviewed                                        |
| `GridPrinter.java`        | Grant, Luci       | Wrote, reviewed                                        |
| `PartyTest.java`          | Tia, Grant        | Wrote, tested                                          |
| `QueueTest.java`          | Tia, Grant        | Wrote, tested, fixed failing test                      |
| `WaitlistTest.java`       | Tia, Luci         | Wrote (W1–W18 plus regression), reviewed              |
| `HistoryTest.java`        | Tia, Nate         | Wrote, tested                                          |

## GenAI Use

We each used GenAI individually and told the team where. In the scope and spec, GenAI helped with project and stretch-goal ideas, finding holes in our bad-input list and test plan, and wording. Nate used Claude to draw the system diagram from a written description and checked it against the spec. Tia used ChatGPT to find missing holes in the test classes she wrote. Grant used ChatGPT on the model/view boundary. For this checkpoint, Claude reviewed the repository against the assignment, found bugs (the size `0` crash, the single-element `Queue.removeIf` case, and removals that skip the `REMOVED` event), and helped draft this README. We checked every suggestion against our code and rewrote it where it didn't match what we built.
