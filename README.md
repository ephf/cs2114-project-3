# Hold Your Spot

**Team:** Luci, Nate, Grant, Tia

Hold Your Spot is a Java console waitlist manager for restaurant hosts. Parties join the back of a single first-come, first-served waitlist and are seated from the front. The waitlist is stored in a custom generic singly linked `Queue<T>` that tracks `front`, `back`, and `size`.

## Design

| Layer      | Classes                                                                         | Role                                                               |
|------------|---------------------------------------------------------------------------------|--------------------------------------------------------------------|
| Model      | `Party`, `Queue<T>`, `Waitlist`, `WaitlistEvent`, `WaitlistListener`, `History` | FIFO order, ticket IDs, validation, change events, seating history |
| Controller | `WaitlistController`                                                            | Reads choices, parses input, calls the model                       |
| View       | `ConsoleView`, `GridPrinter`                                                    | Menus, prompts, formatted output                                   |
| Bootstrap  | `Main`                                                                          | Creates objects and runs the loop                                  |

`enqueue`, `dequeue`, `peek`, `isEmpty`, and `size` are O(1). `removeIf` and `enumerate` are O(n). `Waitlist` fires a `WaitlistEvent` (`ADDED`, `SEATED`, `REMOVED`) to listeners only after a change succeeds. `History` is one of those listeners.

## Reflection

### How did your design absorb the change?

**Feature: Seating History.** The host can view the latest parties that were seated or removed, most recent first.

- **Touched:** `History` (new) and `PartyManager` changed to `WaitlistController` (new menu option, validation, printing, listener registration).
- **Not touched:** `Queue`, `Party`, `Waitlist`, `WaitlistEvent`, `WaitlistListener`.
- **vs. Deliverable 3 prediction:** We predicted `PartyManager` would change, `History` would be new, and `Queue`, `Party`, and `Main` would not change. That matched, other than `Main` getting simplified. The surprise was that the history code was easy only because `WaitlistEvent` and `WaitlistListener` already existed. `PartyManager` used `Queue<Party>` directly, so the model never fired events, and we had to route all add, seat, and remove actions through `Waitlist` first. That rewiring was the real cost of the change.

### Did the change cross the model/view line?

Once. The view had to learn about a new history record type so it can print entries. It does not know how `History` stores records or that it listens to `Waitlist`, and `Queue` internals never reached the view. The layers were mixed in `PartyManager`, which ran prompts, printed output, and held the name-matching rule. We moved matching into `Waitlist` (`findByName`, `removeParty` by ticket).

### What changed from the Deliverable 3 plan?

- **Structure:** We kept `Queue<T>` (generic), and `Main` and implemented `ConsoleView`, `WaitlistController`.
- **Queue methods:** We implemented `removeIf` and `enumerate` instead of `remove(ticketId)`, `findByName`, and `toList`. `Waitlist` builds those operations on top, which keeps restaurant rules out of the queue.
- **Ticket IDs:** They live in `Waitlist`'s private `Entry` (never reused), not in `Party`.
- **Validation:** Name and size checks are in `Waitlist.addParty`, not `Party.validateName` and `validateSize`.
- **Events:** `WaitlistEvent` also carries the ticket ID, and `WaitlistListener` has one method, `onWaitlistEvent`.
- **Tests:** Normal, bad-input, and edge tests exist for `Party`, `Queue`, `Waitlist` (W1-W18 plus a tail-removal regression), and `History`.

### What doesn't work yet?

- Duplicate-name confirmation and the help option are not implemented.
- Ticket numbers aren't shown in the view, so duplicate names are hard to tell apart when removing.

## Next Steps

### What is left for the final build?

1. Show ticket numbers in the list and the remove flow.
2. Duplicate-name confirmation.
3. Help option.
4. History polish and tests (empty history, N too large, N at most 0 or non-numeric).
5. Message polish and a final full test run.

### Which stretch goals are you committing to, and which are you dropping?

- **Committing:** Waitlist history (this checkpoint's feature) and no-show handling, if time allows.
- **Dropping:** Seating by table size (conflicts with FIFO), wait-time calculator (needs restaurant-specific data), and table assignment tracking (keeps scope small).

### What will your 3-minute demo show?

1. Start with an empty list and try **next** and **seat** (friendly messages, no crash).
2. **Add** three parties, including a blank name, a size of `abc` or `0`, and a duplicate name (confirmation prompt).
3. **View** the waitlist, then view next without removing.
4. **Remove** a party from the middle by ticket number and view again.
5. **Seat** two parties, then open **Seating History** (the new feature), including an invalid N.
6. Run the JUnit suite, then quit.

### Who does what?

| Item                                                                          | Owner       |
|-------------------------------------------------------------------------------|-------------|
| Route console through `Waitlist`, blank-name handling, duplicate confirmation | Grant       |
| `Queue.removeIf` fix, `Party` validation                                      | Luci        |
| Seating history (`History` + menu), help option                               | Nate        |
| No-show handling (if time allows)                                             | Luci        |
| Tests, `History` tests, final test run                                        | Tia         |
| Message polish, demo script                                                   | Grant, Nate |

## Attribution

| Class or test file         | Team member(s)    | Contribution                                           |
|----------------------------|-------------------|--------------------------------------------------------|
| `Main.java`                | Grant, Luci       | Wrote, revised                                         |
| `Party.java`               | Luci, Nate, Grant | Wrote (record), reviewed                               |
| `Queue.java`               | Grant             | Wrote; fixed `removeIf` tail case                      |
| `Waitlist.java`            | Tia, Luci         | Wrote (ticket IDs, events, validation); GenAI-assisted |
| `WaitlistEvent.java`       | Tia               | Wrote                                                  |
| `WaitlistListener.java`    | Tia, Luci         | Wrote                                                  |
| `History.java`             | Nate              | Wrote; GenAI-assisted                                  |
| `WailtListController.java` | Nate, Grant       | Wrote, revised, rewired to `Waitlist`                  |
| `ConsoleView.java`         | Grant, Luci       | Wrote, reviewed                                        |
| `PartyTest.java`           | Tia, Grant        | Wrote, tested                                          |
| `QueueTest.java`           | Tia, Grant        | Wrote, tested, fixed failing test                      |
| `WaitlistTest.java`        | Tia, Luci         | Wrote (W1-W18), reviewed                               |
| `HistoryTest.java`         | Tia, Nate         | Wrote, tested                                          |

## GenAI Use

We each used GenAI individually and told the team where. In the scope and spec, GenAI helped with project and stretch-goal ideas, finding holes in our bad-input list and test plan, and wording. Nate used Claude to draw the system diagram from a written description and checked it against the spec. Tia used ChatGPT to find missing holes in the test classes she wrote. Grant used ChatGPT on the model/view boundary. For this checkpoint, Claude reviewed the repository against the assignment, found bugs (the `Queue.removeIf` head case and the console not yet using `Waitlist`), and helped draft this README. We checked every suggestion against our code and rewrote it where it didn't match what we built.
